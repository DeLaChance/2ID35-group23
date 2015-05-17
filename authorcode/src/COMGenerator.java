import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * 该类用以生成COM文件
 */
public class COMGenerator extends VectorLabeledGraph5{

	DataVertex[] input_graph;
	int total_col_num_global = 0;
	long interval_start = 0;
	long cur_cid = 0;
	
	public COMGenerator(File xmlFile) {
		
		super(xmlFile);;
		this.dfs2();
		this.graphReconstruction();
		this.genAdjList();
		this.DFSTopologySort2();
		long time1 = System.currentTimeMillis();
		this.labelGraph2();
		long time2 = System.currentTimeMillis();
		System.out.println("label uses time "+(time2-time1));
		input_graph = this.sortNodeByOneNum2();
		System.out.println("input_graph gen finish");
		
	}
	
	public void duplication(DataVertex curVertex, int vertexId){
		
		if(curVertex.zipped){
			Huffman.unzip_CNeighbor(curVertex);
		}

		//开始duplication
		curVertex.pos_of_cons_Ci = new int[curVertex.CNeighbor_length];
		curVertex.linked = new byte[curVertex.CNeighbor_length];
		curVertex.Fixed = new int[curVertex.CNeighbor_length];
		curVertex.Free = new int[curVertex.CNeighbor_length];
		for(int j=0; j<curVertex.CNeighbor_length; j++){
			curVertex.pos_of_cons_Ci[j] = -1;
			curVertex.linked[j] = 0;
		}
	}
	
	
	public void merge(int a, int scaningfactor){
		
		long time1 = System.currentTimeMillis();
			
		System.out.println(input_graph.length);
		
		for(int i=a; i<input_graph.length-1; i++){
			
			System.out.print(i+" ");
			
			DataVertex ri = input_graph[i];
			
			//这里注释掉，因为不注释掉会出现1616，9068交替出现的情况，而不是10648连续出现
			//会影响后面的COM文件生成，因为这里是一次性生成COM文件，所以要求10648
			//if(i==0){				
				duplication(ri, i);
			//}			
			
			DataVertex cons_ri = input_graph[i+1];
			duplication(cons_ri, i);
			
			mergeSearch(ri, cons_ri);
			
			int count = 0;
			int count_free = 0;

			for(int j=0; j<ri.CNeighbor_length; j++){
				
				if(ri.linked[j] == 0){
					
					int pos = ri.pos_of_cons_Ci[j];
					if(pos != -1){
						
						//进行merge工作
						cons_ri.Fixed[cons_ri.fix_index++]=cons_ri.CNeighbor[pos];						
						cons_ri.linked[pos] = 1;
						count++;
						
					}	
					else{
						ri.Free[ri.free_index++]=ri.CNeighbor[j];
						count_free++;
					}
				}
			}
			System.out.println(ri.CNeighbor_length+" "+count+" "+count_free);
			
			/*//看看从哪个点处抽取Fix，来生成COM文件 
			if(i == 1351585){
				try{
					BufferedWriter out = new BufferedWriter(
							new FileWriter(
									new File("COM_"+scaningfactor+".txt")));
				
					for(int k=0; k<cons_ri.fix_index; k++){
						out.write(cons_ri.Fixed[k]+" ");
					}
					
					out.close();
					return;
				}
				catch(IOException e){System.out.println(e);}
			}*/
			
			
			ri.CNeighbor = null;
			ri.size = null;
			ri.pos_of_cons_Ci = null;
			ri.linked = null;
			ri.Fixed = null;
			ri.Free = null;
					
		}
		long time2 = System.currentTimeMillis();
		System.out.println("merge use time "+(time2-time1)+" ms");
		
	}
	
	public void mergeSearch(DataVertex ri, DataVertex cons_ri){
		
		for(int j=0; j<ri.CNeighbor_length;j++){
				
			ri.pos_of_cons_Ci[j] = findOccur(ri.CNeighbor[j], cons_ri);
		}							
	}
	
	public int findOccur(int Ci, DataVertex ri){
		
		int index = -2;
		
		while(index == -2 ){
			
			if(ri.marker < ri.CNeighbor_length){
				
				//如果marker小于数组的长度

				if(ri.CNeighbor[ri.marker] == Ci){
					//index = ri.marker;
					return ri.marker;
				}
				else if(ri.CNeighbor[ri.marker] > Ci){
					//index = -1;
					return -1;
				}
				else{
					ri.marker++;
				}
		    }
			else{
				//marker一直加，加到最后越界也没找到说明没有
				//index = -1;
				return -1;
			}
		}
		
		return index;
	}
	

	public static void main(String[] args){
		
		int scaningfactor = 30;
		//File xmlFile = new File("auction.xml");
		//File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");			
		File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
		COMGenerator g = new COMGenerator(xmlFile);
		g.merge(449841,scaningfactor);

	}
}
