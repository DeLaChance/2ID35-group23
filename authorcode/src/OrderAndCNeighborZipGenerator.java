import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

/**
 * This file modifies the "PrimeLabeledGraph" class. Prime number will not be used
 * in this class. Just use binary vector.
 * @author ypeng
 *
 */

public class OrderAndCNeighborZipGenerator extends DAG2{
	
	int leafnum = 0;
	int position = 0; //position�����ܹ�ʹ���˶��ٸ���������
	int largest_delta = 0;
	HashMap<Integer, List<DataVertex> > adjList = new HashMap();//Ϊ�˽��зǵݹ���������򣬸���һ��ͼ���ڽ�����
	byte[] array1;
	int ones_num;
	
	Integer[] COM;
	byte[] array; //���Կ��ٰѵ������ֵĵ��label�е�COMԪ���׵�
	
	ArrayList<DataVertex> stk3; //������浹�����������
	Stack<Tmp> stack = new Stack();
	
	public OrderAndCNeighborZipGenerator(File xmlFile){
		super(xmlFile);
		stk3 = new ArrayList();
		
	}
	
	public void genAdjList(){
		
		long time1 = System.currentTimeMillis();
		
		int count = 0;
		System.out.println("genAdj "+this.idVertexMap.size());
			
		ArrayList<DataVertex> allNode = new ArrayList(this.getVertexSet());
		for(int i=0; i<allNode.size(); i++){
			
			DataVertex curVertex = allNode.get(i);
			List<DataVertex> outgoing = this.getOutgoingAdjacentVertices(curVertex);
			adjList.put(curVertex.id, outgoing);
			count = count + outgoing.size();
		}
		
		long time2 = System.currentTimeMillis();
		System.out.println("Adjacent list generate finished "+count+", time use "+(time2-time1)+" ms");
	}
	
	
	
	//�ǵݹ�����������
	public void DFSTopologySort2(){
		
		long time1 = System.currentTimeMillis();
		
		int index = 0; //��ʱ���������磬��n���ڽ������ϴη��ʵ����ĸ�λ��
		int top = 0;
		
		DataVertex rootNode = this.root;
		Tmp firstTmp = new Tmp();
		firstTmp.head = -1;
		firstTmp.node = rootNode;
		firstTmp.pos = index;
		firstTmp.node.topology_visited = true;

		stack.push(firstTmp);
		top++; //���stk��ջ���ģ���ʵû����ô�
		
		List<DataVertex> list = adjList.get(rootNode.id);

		while(top > 0 || index < list.size()){
			
			while( index < list.size() ){
				
				DataVertex curVertex = list.get(index);
				
				if(curVertex.topology_visited == true){
					
					index++;
				}
				else{
					
					curVertex.topology_visited = true;
					
					Tmp newTmp = new Tmp();
					newTmp.head = stack.peek().node.id;
					newTmp.node = curVertex;
					newTmp.pos = index;
					
					stack.push(newTmp);
					top++;
					
					list = adjList.get(curVertex.id);
					index = 0;
					
				}				
			}
			
			if(top > 0){
				
				top--;
				Tmp curTmp = stack.pop();
				stk3.add(curTmp.node);
				list = adjList.get(curTmp.head);				
				if(list == null){
					break;
				}
				
				index = curTmp.pos+1; //��1��ʾn�ڵ���ڽ��������һ��
			}									
		}
		
		long time2 = System.currentTimeMillis();
		System.out.println("topology sort finished, time use "+(time2-time1)+" ms");
	}	


	public void labelGraph2(){
		
		//���Ȱ�adjList���
		//adjList.clear();
		
		
		double nz = 0;
		int count = 0;
		int time_for_gc = 0;
		System.out.println("stk3 size is "+stk3.size());
		array1 = new byte[this.idVertexMap.size()];
			
		
		while(count < stk3.size()){
		
			DataVertex curVertex = stk3.get(count);
			
			List<DataVertex> outGoingNeighbour = this.getOutgoingAdjacentVertices(curVertex);
		
			if(outGoingNeighbour.size() == 0){//˵������Ҷ�ӽڵ�
				curVertex.CNeighbor=new int[1];
				curVertex.CNeighbor[0] = position;
				curVertex.CNeighbor_length = 1;
				//flushLabelToFile(curVertex);
				curVertex.haslabeled = true;
				//curVertex.largest_position = position;
				position++;
				leafnum++;
				count++;
				nz++;
				
				//�����ǲ���Ҫzip�ģ�����Ϊ�˰�labelд���ļ�������е�Ϊ���ģ��е�Ϊzip���ģ��͸ɴ�zip����
				Huffman.zip_CNeighbor(curVertex);
				curVertex.zipped = true;
				
			}
			else{//˵���Ƿ�Ҷ�ڵ�
								
				childrenVectorlabelProduct11(curVertex);
				
				curVertex.CNeighbor[curVertex.CNeighbor.length-1] = position;
				nz=nz+curVertex.CNeighbor.length;
				
				curVertex.CNeighbor_length = curVertex.CNeighbor.length;
				
				Huffman.zip_CNeighbor(curVertex);
				curVertex.zipped = true;
				
	//			curVertex.largest_position = position;
				position++;
				
				curVertex.haslabeled = true;
				count++;
				//System.out.println("we are at "+count+" "+curVertex.CNeighbor_length+" "+curVertex.CNeighbor_zip.size());
				//System.out.println("we are at "+count+" "+curVertex.CNeighbor_length);
				
			}
			
		}
		
		System.out.println("total num of labeled nodes is "+count);
		System.out.println("leaf nodes has "+leafnum);
		System.out.println("position is "+position);	
		System.out.println("nz has "+nz);	
		
	}
	
	
	public void childrenVectorlabelProduct11(DataVertex curNode){
		
		//ArrayList<DataVertex> outgoingNodes = new ArrayList<DataVertex>(this.getOutgoingAdjacentVertices(curNode));
		//List<DataVertex> outgoingNodes = adjList.get(curNode.id);
		DataVertex[] outgoingNodes = new DataVertex[adjList.get(curNode.id).size()];
		adjList.get(curNode.id).toArray(outgoingNodes);
		if(outgoingNodes.length == 0){
			
			//�����Ҷ�ӽڵ㣬��Ӧ��û����
			return;
		}
		else{
			
			//���Ȱ�array1����,��Ȼ�ļ��洢����������������01����
			//����ת��01����֮���ӿ�child product���ٶ�
			
			//��һ�������ʱ���Ѿ�������array1��
			/*for(int i=0; i<array1.length; i++){
				array1[i] = 0;
			}*/
			
			//Ҳ�Ǹ��Ż���Ϊ�˱������һ��
			//��array1ת��curNode��CNeighbor��ʱ��,��ͷ��β����array1
			int start = Integer.MAX_VALUE; int end = 0; 
						
			for(int i=0; i<outgoingNodes.length; i++){
				
				DataVertex curVertex = outgoingNodes[i];
				
				//����Ҫ��curVertex��label���ϵ�array1��ȥ====��ʼ
				if(curVertex.zipped){
					
					Huffman.unzip_CNeighbor(curVertex);
				}
				for(int j=0; j<curVertex.CNeighbor.length; j++){
					if(array1[curVertex.CNeighbor[j]] == 0){
						array1[curVertex.CNeighbor[j]] = 1;
						
						if(curVertex.CNeighbor[j] < start){
							start = curVertex.CNeighbor[j];
						}
						if(curVertex.CNeighbor[j] > end){
							end = curVertex.CNeighbor[j];
						}
						
						ones_num++;
					}
				}
				//������֮��Ҫ��curVertex��CNeighbor��յ�
				if(curVertex.zipped){
					curVertex.CNeighbor = null;
				}				
				//����Ҫ��curVertex��label���ϵ�array1��ȥ====����				
			}
			
			//��array1ת��curNode��CNeighbor
			curNode.CNeighbor = new int[ones_num+1]; //Ϊ�˸������ټӸ�������������
			int pos = 0;
			//for(int i=0; i<array1.length; i++){
			for(int i=start; i<=end; i++){
				if(array1[i] == 1){
					curNode.CNeighbor[pos] = i;
					pos++;
					
					//����array1�����Ԫ�أ���������Ͳ��õ���ȥ����array1��
					array1[i] = 0;
				}
			}
					
			ones_num = 0;		
		}	
	}
	
	
	
	
	public DataVertex[] sortNodeByOneNum2(){
		
		DataVertex[] orderedVertexList = new DataVertex[this.idVertexMap.size()];
		Iterator it = this.idVertexMap.values().iterator();
		for(int i=0; i<orderedVertexList.length; i++){
			orderedVertexList[i] = (DataVertex) it.next();
		}
		
		Arrays.sort(orderedVertexList);
		
		return orderedVertexList;
		
	}
	
	
	public void outCNeighborZipToFile(DataVertex[] orderedVertexList,
			int start_of_third, int scaningfactor){
		
		initCOM("COM_"+scaningfactor+".txt");
		
		outCNeighborZipToFileFirstAndTwo(orderedVertexList,
				start_of_third, "CNeighborZipFS_"+scaningfactor+".txt");
		
		outCNeighborZipToFileThird(orderedVertexList,
				start_of_third, "CNeighborZipT_"+scaningfactor+".txt");
	}
	
	
	public void outCNeighborZipToFileFirstAndTwo(DataVertex[] orderedVertexList,
			int start_of_third, String filename){
		
		try{
			/*DataOutputStream out=new DataOutputStream(   
                    new BufferedOutputStream(   
                    new FileOutputStream(filename)));  
			
			//��һ�ڶ�����дѹ���������
			for(int i=0; i<start_of_third; i++){
				
				DataVertex curVertex = orderedVertexList[i];
				
				out.writeInt(curVertex.CNeighbor_zip.length);
				for(int j=0; j<curVertex.CNeighbor_zip.length; j++){
					out.writeInt(curVertex.CNeighbor_zip[j].digit);
					out.writeInt(curVertex.CNeighbor_zip[j].occur_finish_position);
				}
			}
		
			out.close();*/
			
			BufferedWriter out = new BufferedWriter(
					new FileWriter(
					new File(filename)));
			
			for(int i=0; i<start_of_third; i++){
				
				DataVertex curVertex = orderedVertexList[i];
				Huffman.unzip_CNeighbor(curVertex);
				
				for(int j=0; j<curVertex.CNeighbor_length; j++){
					out.write(curVertex.CNeighbor[j]+" ");
				}
				out.write(" \n");
			}
				
			out.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void outCNeighborZipToFileThird(DataVertex[] orderedVertexList,
		int start_of_third, String filename){
			
		try {
			BufferedWriter out = new BufferedWriter(
			new FileWriter(
			new File(filename)));
			
			
			//��������д��ѹ��������,�׵�COM֮���
			for(int i=start_of_third; i<orderedVertexList.length; i++){
				
				DataVertex curVertex = orderedVertexList[i];
				
				Huffman.unzip_CNeighbor(curVertex);
				
				for(int j=0; j<curVertex.CNeighbor_length; j++){
					
					if(array[ curVertex.CNeighbor[j] ] != 1){
						out.write(""+curVertex.CNeighbor[j]+" ");
					}
				}
				out.write(" \n");
				
				curVertex.CNeighbor = null;				
			}
			
			out.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	public void outOrderFile(DataVertex[] orderedVertexList, String filename){
		
		//�����orderedVertexList[i].id �䵽order�ļ���
		try{
			BufferedWriter out = new BufferedWriter(
					new FileWriter(
					new File(filename)));
			
			for(int i=0; i<orderedVertexList.length; i++){
				
				out.write(""+orderedVertexList[i].id+" "+orderedVertexList[i].CNeighbor_length+"\n");
			}
			
			out.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	//��ʼ��COM���ϣ����ļ��ж�ȡ
	public void initCOM(String filename){
		
		try{
			
			ArrayList<Integer> tmpCOM = new ArrayList();
			
			BufferedReader in = new BufferedReader(
							new FileReader(
							new File(filename)));
			
			String line = in.readLine();
			String[] tmp;
			
			while(line != null){
				
				tmp = line.split(" ");
				for(int i=0; i<tmp.length; i++){
					
					tmpCOM.add(Integer.parseInt(tmp[i]));
				}
				
				line = in.readLine();
			}
			
			in.close();
			
			COM = new Integer[tmpCOM.size()];
			tmpCOM.toArray(COM);						
			System.out.println("COM has "+tmpCOM.size());
			
			//��ʼ��array 0/1 ����=======��ʼ
			array = new byte[this.idVertexMap.size()];
			for(int i=0; i<array.length; i++){
				array[i] = 0;
			}
			for(int i=0; i<COM.length; i++){
				array[COM[i]] = 1;
			}
			//��ʼ��array 0/1 ����=======����
			
		}catch(IOException e){System.out.println(e);}	
		
	}
	
	
	public static void main(String args[]){
		
		
	    int start_of_third = 449841;
	    int scaningfactor = 30;
		
		
		//File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");
		File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		//File xmlFile=new File("auction.xml");	    
		
		OrderAndCNeighborZipGenerator g = new OrderAndCNeighborZipGenerator(xmlFile);
	    
	    g.dfs2();
	    g.graphReconstruction();
	    System.out.println("Different Label num is: "+DataVertex.labelidmap.size());
	    
	    
	    g.genAdjList();
	    g.DFSTopologySort2();
	    long time = System.currentTimeMillis();
	    g.labelGraph2();
	    long time2 = System.currentTimeMillis();
	    long timeused = (time2-time);
	    System.out.println("time used to label "+timeused+" ms");
	 
	    DataVertex[] input_graph = g.sortNodeByOneNum2();
	    
 
		g.outCNeighborZipToFile(input_graph,start_of_third,scaningfactor);
		g.outOrderFile(input_graph, "Order_"+scaningfactor+".txt");
	    
	}
	
}
