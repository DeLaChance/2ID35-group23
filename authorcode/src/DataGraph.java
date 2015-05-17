import salvo.jesus.graph.*;
import org.jdom.*;
import java.io.*;
import java.util.*;
import org.jdom.input.SAXBuilder;
import salvo.jesus.graph.DirectedEdge;


/**
 * This class is basically copied from the original code.
 * I remove some unrelated functions.
 */
public class DataGraph
    extends DirectedGraphImpl {
  protected Document doc = null;
  protected DataVertex root = null;

  protected ArrayList<Edge> IdrefEdges = null;


  // Map( ID:String, Element), only for IDREF/ID
  private HashMap<String, DataVertex> idmap = null;
  
  // Map(ID, 该ID所对应的vertex) 例如：(root.id, root)
  protected HashMap<Integer, DataVertex> idVertexMap;

  protected boolean isValid = false;
  private boolean showAttr = false;

  public DataGraph() {
	  this.idVertexMap=new HashMap<Integer, DataVertex>();
	  this.idmap=new HashMap<String,DataVertex>();
  }

  public DataGraph(File file) {
    try {
      
      SAXBuilder builder = new SAXBuilder();
      builder.setExpandEntities(false);
      doc = builder.build(file);
      if (doc == null) {
        throw new Exception("DataGraph: error to create doc!\n");
      }
      builder = null;
    }
    catch (Exception e) {
      e.printStackTrace();
      return;
    }

    if (doc == null) {
      return;
    }

    // generate data graph
    System.out.print("DOM tree complete!\n");
    genGraph(false);
    doc = null;
  }

  /**
   * Generate the data graph based on DOM tree
   * We deal with cross references in the DOM tree by adding an edge
   * from IDREF element to ID element
   * @param showAttr (true, if graph will show attributes as vertices)
   * @return true if succeed
   */
  public boolean genGraph(boolean showAttr) {
    // no change, nothing need to do
    if (isValid && (this.showAttr == showAttr)) {
      return true;
    }

    // DOM tree is invalid
    if (doc == null) {
      System.out.print("DOM tree is empty!\n");
      return false;
    }

    /* generate the graph */
    // generate the corresponding tree
    Element root = doc.getRootElement();
    if (root == null) {
      System.out.print("genGraph(): doc root is null!\n");
      return false;
    }

    DataVertex rootv = new DataVertex(root);
    this.idVertexMap=new HashMap<Integer,DataVertex>();
    try {
      add(rootv);
      this.idVertexMap.put(rootv.id, rootv);
      this.root = rootv;
      boolean ret = setupGraph(rootv, showAttr);

      System.out.print("Build original datagrap complete!\n");
      if (ret == false) {
        return ret;
      }

      // build the hash table which map (ID, Element)
      this.idmap=new HashMap<String,DataVertex>();
      buildIDMap(this.root);
      System.out.print("Build idmap complete, size:" + idmap.size()+"!\n");

      // recursively add the cross edges based on (IDREF, ID)
      IdrefEdges = new ArrayList<Edge>();
      addCrossEdge();
      System.out.print("Build crossedge complete:"+IdrefEdges.size()+"!\n");
      Iterator<DataVertex> it = this.getVerticesIterator();
      while (it.hasNext()){
    	 (it.next()).setObject(null);
      }
       
    }
    catch (Exception e) {
      e.printStackTrace();
      root = null;
      idmap = null;
      return false;
    }

    // set private tags
    this.showAttr = showAttr;
    this.isValid = true;

    // print info
    System.out.print("Graph has nodes " + getVerticesCount()+"\n");
    System.out.print("Graph has nodes " + this.idVertexMap.size()+"\n");

    return true;
  }

  // recursively generate the graph from root to leaves
  private boolean setupGraph(DataVertex v, boolean showAttr) throws Exception {
    // NOTE:here may have some problems
    org.jdom.Element ele = (org.jdom.Element) v.getObject();
    if (ele == null) {
      System.out.println("setupGraph(): Element is null!\n");
      return false;
    }

    // add elements
    Iterator it = ele.getChildren().iterator();
    while (it.hasNext()) {
      Element ch = (Element) it.next();
      if (ch == null) {
        continue;
      }
      DataVertex newv = new DataVertex(ch);
      add(newv);
      this.idVertexMap.put(newv.id, newv);
      addEdge(v, newv); // source -> target

      boolean ret = setupGraph(newv, showAttr);
      if (ret == false) {
        return ret;
      }
    }

    if (showAttr == false) {
      return true;
    }

    // add Attributes
    it = ele.getAttributes().iterator();
    while (it.hasNext()) {
      Attribute at = (Attribute) it.next();
      if (at == null) {
        continue;
      }

      DataVertex newv = new DataVertex(at);
      add(newv);
      addEdge(v, newv);
    }

    return true;
  }

  /**
   * Build an internal hash table for looking for ID strings
   * For example:
   *  <item id="item0">
   * the pair of ("item0", Vertex) will be inserted into this
   * hash table.
   *
   * Suppose DOM tree is valid.
   */
  protected void buildIDMap(DataVertex v) {
    org.jdom.Element ele = (org.jdom.Element) v.getObject();
    Iterator it = ele.getAttributes().iterator();
    while (it.hasNext()) {
      Attribute at = (Attribute) it.next();
      if (at == null) {
        continue;
      }

      if (at.getAttributeType() == Attribute.ID_ATTRIBUTE) {
        idmap.put(at.getValue(), v);
      }
    }

    // recursively add children's ID strings
    it = getOutgoingAdjacentVertices(v).iterator();
    while (it.hasNext()) {
      DataVertex vt = (DataVertex) it.next();
      if (vt == null) {
        continue;
      }

      buildIDMap(vt);
    }
  }
  /**
   * Add cross edges from Vertex v based on cross references in DOM tree
   * For example:
   *  <a id="a0" />
   *  ...
   *  <b idref="a0" />
   * An edge (b,a) should be added.
   * @param v (source vertex)
   * @return true if succeed
   * @throws Exception
   */
  protected boolean addCrossEdge() throws Exception {

    DataVertex v;
    Iterator<DataVertex> it1 = this.getVerticesIterator();
    while (it1.hasNext()) {
      v = it1.next();
      org.jdom.Element ele = (org.jdom.Element) v.getObject();
      if (ele == null) {
        throw new Exception("addCrossEdge(): Element is null!\n");
      }

      // look for IDREF attribute
      Iterator it = ele.getAttributes().iterator();
      while (it.hasNext()) {
        Attribute at = (Attribute) it.next();
        if (at == null) {
          continue;
        }

        // add an edge (IDREF, ID)
        if (at.getAttributeType() == Attribute.IDREF_ATTRIBUTE) {
          DataVertex to = idmap.get(at.getValue());
          if (to == null) {
            throw new Exception("addCrossEdge(): NULL data node!\n");
          }
          if (v != to && getEdge(v, to) == null) { // do not allow self-cycles
            DirectedEdge e = (DirectedEdge) addEdge(v, to);
            IdrefEdges.add(e);
          }
        }
      }
    }
    return true;
  }

  
  
  public DataVertex getRoot() {
    return root;
  }

  public Document getDoc() {
    return doc;
  }


  public static void main(String[] args) {
	  //File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark3\\xmark0.xml");
	  File xmlFile = new File("../data/xmark80/xmark0.xml");
	  //File xmlFile=new File("auction.xml");
      DataGraph dataGraph1 = new DataGraph(xmlFile); 
      /*for(int i=0;i<dataGraph1.getVerticesCount();i++){
    	  System.out.print(dataGraph1.idVertexMap.get(i)+"\n");
      }*/
  }

}
