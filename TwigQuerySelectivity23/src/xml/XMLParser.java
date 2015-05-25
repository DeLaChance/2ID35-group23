package labeler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import labeler.Edge;
import labeler.Node;

public class XMLParser {
    String xmlFile = null;
    Document dom;
    List<Node> nodes = null;
    List<Edge> edges = null;
    Element root = null;
    NodeList children = null;
    int nodeId = 0;
    
    public void XMLParser(double scaningfactor) throws SAXException, IOException, ParserConfigurationException {
        xmlFile = "../datasets/xmark-sf"+scaningfactor+".xml";
        parseXmlFile();
    }
    
    /**
     * Parses the XML file
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException 
     */
    private void parseXmlFile() throws SAXException, IOException, ParserConfigurationException{
            //get the factory
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            try {
                    //Using factory get an instance of document builder
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    //parse using builder to get DOM representation of the XML file
                    dom = db.parse(xmlFile);
                    root = dom.getDocumentElement();  
                    children = root.getChildNodes();
            }catch(ParserConfigurationException pce) {
                    pce.printStackTrace();
            }catch(SAXException se) {
                    se.printStackTrace();
            }catch(IOException ioe) {
                    ioe.printStackTrace();
            }
            
            parseDocument(children, nodeId);
    }
    
    /**
     * Returns the root of the XML file.
     * @return the root of the XML file.
     */
    private Element getRootElement() {
        return root;
    }
    
    /**
     * Returns the children of a given Element
     * @param Element the node for which you want to retrieve the children.
     */
    private NodeList getChildren() {
        return children;
    }
    
    /**
     * Generates the children node of the parent and makes an edge from the child
     * to the parent.
     * @param parent the parent node of all children.
     * @param children the child node of the parent.
     */
    private void parseDocument(NodeList children, int parentNodeId){
            NodeList newList = children;
            if(newList != null && newList.getLength() > 0) {
                    for(int i = 0 ; i < newList.getLength();i++) {
                            //child is the current handled node.
                            Element child = (Element)newList.item(i);
                            //childId is the Id of the handled node.
                            int childId = parentNodeId++;
                            String childName = child.getNodeName();
                            String nodeAttributes = getTextValue(child, childName);
                            
                            //Create node.
                            createNode(nodeAttributes, childId);

                            //create Edge from parent to child
                            createEdge(parentNodeId, childId);
                            
                            //Take childeren of current node if exists.
                            if(child.getChildNodes().getLength() > 0) {
                                parseDocument(child.getChildNodes(), childId);
                            } else {
                                System.out.println("This is a leaf!");
                            }
                    }
            }
    }
    
    private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
    
    public void createNode(HashMap<String,String> attributes, int id) {
        Node node = new Node(attributes, id);
        nodes.add(node);
    }
    
    public void createEdge(int parentId, int childId) {
        Edge edge = new Edge(parentId, childId);
        edges.add(edges);
    }
    
    public List<Node> returnNodes() {
        return nodes;
    }
    
    public List<Edge> returnEdges() {
        return edges;
    }
}