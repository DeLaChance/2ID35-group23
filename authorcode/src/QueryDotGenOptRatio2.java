import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 生成1000个query，不论有效无效全算进去
 * @author ypeng
 *
 */
public class QueryDotGenOptRatio2 {

	
	public static void main(String[] args){
		
		int[] scanfactor = {90, 80, 70, 60, 50, 40, 30, 20, 10};
		int[] startOfThird = {1350212, 1201348, 1052521, 898589, 747755, 598055, 449841, 301231, 151179};
		
		
		int scaningfactor = scanfactor[8];
		int start_of_third = startOfThird[8];
		//int scaningfactor = 1;
		//int start_of_third = 15444;
		
		//String address = "D:/MyProject/SelectivityEst4/lab/";
		String address = "./lab/";
		
		//File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");	
		//File xmlFile = new File("auction.xml");
		File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
		MergingPro9 m = new MergingPro9(xmlFile, scaningfactor, start_of_third);
		m.merge();
		
		
		//现在要进行simple path的测试
		Gen2 g = new Gen2(2,2,DataVertex.labelToVertexIDMap,DataVertex.labelidmap);
		Query q;
		
		int queryDotNum_beforeOpt = 0;
		int queryDotNum_afterOpt = 0;
		
		for(int i=0; i<1000; i++){ 
			
			q = g.getQuery();
			System.out.println("\n"+q);
			
			m.output_query_position(q.labels.get(0), address);
			m.output_target_position(q.labels.get(1), address);
			
			m.output_query_position2(q.labels.get(0), q.labels.get(1), address);
			
			queryDotNum_beforeOpt += getFileLineNum(address+q.labels.get(0)+"_queryCOM.txt");
			queryDotNum_beforeOpt += getFileLineNum(address+q.labels.get(0)+"_queryNormal.txt");
			
			queryDotNum_afterOpt += getFileLineNum(address+q.labels.get(0)+"_queryCOM2.txt");
			queryDotNum_afterOpt += getFileLineNum(address+q.labels.get(0)+"_queryNormal2.txt");
		}
		
		System.out.println("before opt: "+queryDotNum_beforeOpt);
		System.out.println("after opt: "+queryDotNum_afterOpt);
	}
	
	public static int getFileLineNum(String filename){
		
		int line_num = 0;
		
		try{

			BufferedReader in = new BufferedReader(
					new FileReader(
					new File(filename)));
			
			String line = in.readLine();
			
			while(line != null){
				
				line_num++;
				line = in.readLine();
			}
			
			
		}
		catch(IOException e){
			System.out.println(e);
		}
		
		return line_num;
	}
}
