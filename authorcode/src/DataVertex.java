/**
 * This class is for node of XML Graph
 */
import java.util.*;

import salvo.jesus.graph.*;
import org.jdom.*;

public class DataVertex extends VertexImpl implements Comparable<DataVertex>{

  public String label;
  public int labelid;
  public int SCCCorrespond = -1;//���vertex����Ӧ��SCC��id��idָSCC��SCCList�е�id��
  
  
  protected int id;
  //public ArrayList<Integer> CNeighbor = new ArrayList<Integer>();
  public int[] CNeighbor = null; //���������������,��������Ŀ�ٵ�ʱ�����
  public int CNeighbor_length = 0;
  public int CNeighbor_length_without_COM = 0;
  public byte[] bitvector = null; //��������Ŀ�ӽ���ͼ�е����Ŀ�������������
  //public ArrayList<Code> CNeighbor_zip = new ArrayList();
  public Code[] CNeighbor_zip;
  protected boolean haslabeled = false;
  protected boolean visited = false; //����������
  protected boolean flushedLabelToFile = false;
  protected boolean zipped = false;
  
  
	//public CNode[] Fixed = new CNode[0];
	//public CNode[] Free = new CNode[0];
	//public CNode[] Linked;
    //public ArrayList<CNode> Fixed = new ArrayList();
    //public ArrayList<CNode> Free = new ArrayList();
  	public int[] Fixed = new int[0];
  	public int fix_index = 0;
  	public int[] Free = new int[0];
  	public int free_index = 0;
  	
  	//����Ǳ���ǲ��Ǿ���������ֵĵ㣬���ز�������COM����
  	boolean THIRD = false;
   
  
	//��4���Ǵ�RNode�������
//	public CNode[] CNeighborList; //���duplication��
	public int removed_count = 0;
	public int linked_count = 0;
	public int marker = 0; //merge����ʱ����
	
	public byte[] size; 
	public int[] pos_of_cons_Ci;
	public byte[] linked;
	
	//public ArrayList<Integer> primitiveCid = new ArrayList();
	public int[] primitiveCid;
	public ArrayList<Code> primitiveCid_zip = new ArrayList();
	public int[] primitiveCid_zip2_digit;
	public int[] primitiveCid_zip2_pos;
	
	//����������������Ҷ˵�
	public int Left = 0;
	public int Right = 0;
	
	//�⼸����Tarjan��
	int DFN = 0;
	int LOW = 0;
	boolean nrdfs_visited = false;
	int nrdfs_time = 0;
	boolean in_stk2 = false;
	
	//���������������
	boolean topology_visited = false;
	
	//�����Ϊ����child label�Ĳ���
//	int largest_position = -1;
	

  private static int counter = 0;
  protected static HashMap<String,Integer> labelidmap = new HashMap<String,Integer>();
  protected static HashMap<String, ArrayList<Integer> > labelToVertexIDMap = new HashMap();
  
//Construct data vertex from xml document
  public DataVertex(Object obj) {
    super(obj);
    if (obj instanceof Element) {
      label = ( (Element) obj).getName();
    }
    else if (obj instanceof Attribute) {
      label = ( (Attribute) obj).getName();
    }
    else {
      label = obj.toString();
    }
    Integer lid = (Integer)labelidmap.get(label);
    if (lid != null)
      labelid = lid.intValue();
    else {
      labelid = labelidmap.size();
      labelidmap.put(label, new Integer(labelid));
    }
    id = counter;
    counter++;
    
    ArrayList<Integer> tmp = labelToVertexIDMap.get(label);
    if(tmp == null){
    	ArrayList<Integer> newList = new ArrayList();
    	newList.add(id);
    	labelToVertexIDMap.put(label, newList);
    }
    else{
    	tmp.add(id);
    }
  }

  
  public String toString() {
    return "["+"Element"+": <"+label+ id +"/>]";
  }

  public int getId(){
	  return id;
  }
  protected Object clone() throws CloneNotSupportedException{
	    DataVertex newobj = new DataVertex( this.getObject());
	    newobj.label = label;
	    return newobj;
	  }
  
  public DataVertex(SCC2 scc){
	  
	  super();
	  id = counter;
	  counter++;
	  label = "SCC";
	  SCCCorrespond = scc.id;
  }
  
  
  
  public static void main(String[] args) {
    DataVertex dataVertex1 = new DataVertex("label");
    DataVertex dataVertex2 = new DataVertex("label1");
    System.out.print(dataVertex1.labelid);
    System.out.print(dataVertex2.labelid);
  }

@Override
	public int compareTo(DataVertex tarVertex) {
	
		//if(this.CNeighbor.size() > tarVertex.CNeighbor.size()){
		if(this.CNeighbor_length > tarVertex.CNeighbor_length){
			return 1;
		}
		else{
			//if(this.CNeighbor.size() == tarVertex.CNeighbor.size()){
			if(this.CNeighbor_length == tarVertex.CNeighbor_length){
				return 0;
			}
			else{
				return -1;
			}		
		}
	}

}