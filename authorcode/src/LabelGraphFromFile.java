import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;


public class LabelGraphFromFile extends DAG2{

	DataVertex[] input_graph;
	
	public LabelGraphFromFile(File xmlFile){
		super(xmlFile);;
		this.dfs2();
		this.graphReconstruction();
	}
	
	
	public void readOrder(String filename){
		
		try{
			
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File(filename)));
			
			String line = in.readLine();
			String[] tmp;
			
			for(int i=0; i<this.idVertexMap.size(); i++){
				
				tmp = line.split(" ");
				input_graph[i] = this.idVertexMap.get(Integer.parseInt(tmp[0]));
				input_graph[i].CNeighbor_length = Integer.parseInt(tmp[1]);
				
				line = in.readLine();
			}
			
			in.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void readCNeighborZip(int start_of_third, int scaningfactor){
		
		try{

			//读入第一第二部分的点的CNeighborZip
			/*DataInputStream in=new DataInputStream(   
                    new BufferedInputStream(   
                    new FileInputStream("CNeighborZipFS_"+scaningfactor+"B")));   
			
			for(int i=0; i<start_of_third; i++){
				
				int CNeighbor_zip_length = in.readInt();
				input_graph[i].CNeighbor_zip = new Code[CNeighbor_zip_length];
				int index = 0;
				
				for(int j=0; j<CNeighbor_zip_length; j++){
					
					Code newCode = new Code(in.readInt(), in.readInt());
					input_graph[i].CNeighbor_zip[index] = newCode;
					index++;
				}
			}
		
			in.close();*/
			
			//读入没有压缩的明文
			{
			BufferedReader in1 = new BufferedReader(
					new FileReader(
							new File("CNeighborZipFS_"+scaningfactor+".txt")));
			
			String line = in1.readLine();
			String[] tmp;
			
			for(int i=0; i<start_of_third; i++){
				
				DataVertex curVertex = input_graph[i];
				
				tmp = line.split(" ");

				curVertex.CNeighbor = new int[tmp.length];
				int index = 0;
						
				for(int j=0; j<tmp.length; j++){
					
					curVertex.CNeighbor[index] = Integer.parseInt(tmp[j]);
					index++;
				}
				
				line = in1.readLine();
				
			}
				in1.close();
			}
			
			
			
			{
			BufferedReader in2 = new BufferedReader(
					new FileReader(
							new File("CNeighborZipT_"+scaningfactor+".txt")));
			
			String line = in2.readLine();
			String[] tmp;
			
			for(int i=start_of_third; i<input_graph.length; i++){
				
				DataVertex curVertex = input_graph[i];
				
				tmp = line.split(" ");
						
				curVertex.CNeighbor = new int[tmp.length];
				int index = 0;
			
				for(int j=0; j<tmp.length; j++){
					
					curVertex.CNeighbor[index] = Integer.parseInt(tmp[j]);
					index++;
				}
				
				line = in2.readLine();
			}
			
			in2.close();
		}	
		}
		catch(IOException e){
			System.out.println(e);
		}
		
	}
	
	public void label(int scaningfactor, int start_of_third){
		
		input_graph = new DataVertex[this.idVertexMap.size()];
		
		readOrder("Order_"+scaningfactor+".txt");
		
		readCNeighborZip(start_of_third, scaningfactor);
	
	}
	
	public static void main(String[] args){
		
		File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");
		LabelGraphFromFile g = new LabelGraphFromFile(xmlFile);
		g.label(1, 15444);
	}
}
