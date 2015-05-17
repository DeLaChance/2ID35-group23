/*import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

*//**
 * 这个类是看看用了bar统计数据之后，误差多少。预计在diagonal grid evaluation
 * 和 area 方法之间. 本类用mergeing6
 * @author ypeng
 *
 *//*
public class Est6 {
	
	
	public static void main(String[] args){
		
		int scaningfactor = 1;
		int start_of_third = 15444;
		int AllCNodeNum = 106503;
		
		
		String address = "D:/MyProject/SelectivityEst4/lab/";
		//String address = "../lab/";
		
		File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");	
		//File xmlFile = new File("auction.xml");
		//File xmlFile = new File("../data/xmark100/xmark0.xml");
		
		MergingPro9 m = new MergingPro9(xmlFile, scaningfactor, start_of_third);
		m.merge();
		
		
		//现在要进行simple path的测试
		Gen2 g = new Gen2(2,2,DataVertex.labelToVertexIDMap,DataVertex.labelidmap);
		Query q;
		ArrayList<Query> positiveQueryList = new ArrayList();

		for(int i=0; i<1000; i++){ 
			
			q = g.getQuery();
			System.out.println("\n"+q);
			
			m.output_query_position(q.labels.get(0), address);
			m.output_target_position(q.labels.get(1), address);
			
			//这是evaluation
			Check2 c = new Check2(AllCNodeNum);
			c.readQueryFile(address+""+q.labels.get(0)+"_queryCOM.txt");
			//c.readTargetFile(address+""+q.labels.get(1)+"_targetCOM.txt");
			//q.realValue = q.realValue + c.countChild();
			q.realValue = q.realValue + c.readTargetFile(address+""+q.labels.get(1)+"_targetCOM.txt");
			
			Check2 c2 = new Check2(AllCNodeNum);
			c2.readQueryFile(address+""+q.labels.get(0)+"_queryNormal.txt");
			//c2.readTargetFile(address+""+q.labels.get(1)+"_targetNormal.txt");
			//q.realValue = q.realValue + c2.countChild();
			q.realValue = q.realValue + c2.readTargetFile(address+""+q.labels.get(1)+"_targetNormal.txt");

			if(q.realValue > 50*scaningfactor){
				positiveQueryList.add(q);
			}
		}
		
		//这是用bar统计技术的estimation
		for(int k=10; k<=300; k+=10){
		//{	
			int side_length = k;
			
			//这是estimation
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				
				float cur_t = 0;
				float cur_error = 0;
				
				//==估计COM Histogram
				Hist2 hist1COM = new Hist2(side_length);
				hist1COM.loadHist(address+""+postiveQ.labels.get(0)+"_queryCOM.txt");
				Hist2 hist2COM = new Hist2(side_length);
				hist2COM.loadHist(address+""+postiveQ.labels.get(1)+"_targetCOM.txt");
				
				LastHist3 lastHistCOM = new LastHist3(side_length);
				double time1 = System.nanoTime();
				lastHistCOM.est(hist1COM, hist2COM);
				double time2 = System.nanoTime();				
				
				cur_t += (time2-time1);
				cur_error += lastHistCOM.getResult();

				//估计Normal Histogram
				Hist2 hist1Normal = new Hist2(side_length);
				hist1Normal.loadHist(address+""+postiveQ.labels.get(0)+"_queryNormal.txt");
				Hist2 hist2Normal = new Hist2(side_length);
				hist2Normal.loadHist(address+""+postiveQ.labels.get(1)+"_targetNormal.txt");
				
				LastHist3 lastHistNormal = new LastHist3(side_length);
				double time3 = System.nanoTime();
				lastHistNormal.est(hist1Normal, hist2Normal);
				double time4 = System.nanoTime();
				
				cur_t += (time2-time1);
				cur_error += lastHistNormal.getResult();
				
				postiveQ.time = cur_t;
				postiveQ.estValue = cur_error;
				
			}
			
			//System.out.println(positiveQueryList);
			double error = 0;
			double time = 0;
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				double tmp = Math.abs(postiveQ.estValue - postiveQ.realValue);
				double tmp2 = tmp/postiveQ.realValue;
				error = error + tmp2;			
				
				time = time + postiveQ.time;
			}
			
			try{
				File ofile = new File("integration.txt");
				FileWriter fileW = new FileWriter(ofile,true);
				BufferedWriter out = new BufferedWriter(fileW);
				
				out.write(""+side_length+" "+(error/positiveQueryList.size())
						+" "+(time/positiveQueryList.size())+"\n");
				
				out.close();
			}
			catch(IOException e){}
			
			System.out.println("grid size: "+side_length
					+" average error: "+(error/positiveQueryList.size())
					+" time: "+(time/positiveQueryList.size()));
		}
		
		
		
		//这是用面积/child check at diagonal技术的estimation
		for(int k=10; k<=300; k+=10){
			
			int side_length = k;
			
			for(int i=0; i<positiveQueryList.size(); i++){
				Query postiveQ = positiveQueryList.get(i);
				
				Hist2 hist1 = new Hist2(side_length);
				hist1.loadHist(address+""+postiveQ.labels.get(0)+"query.txt");
				Hist2 hist2 = new Hist2(side_length);
				hist2.loadHist(address+""+postiveQ.labels.get(1)+".txt");
				
				LastHist lastHist = new LastHist(side_length);
				double time1 = System.nanoTime();
				lastHist.est(hist1, hist2);
				double time2 = System.nanoTime();
				postiveQ.time = time2-time1;
				postiveQ.estValue = lastHist.result();				
			}
			
			//System.out.println(positiveQueryList);
			double error = 0;
			double time = 0;
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				double tmp = Math.abs(postiveQ.estValue - postiveQ.realValue);
				double tmp2 = tmp/postiveQ.realValue;
				error = error + tmp2;
				
				time = time + postiveQ.time;
			}
			
			try{
				File ofile = new File("area.txt");
				//File ofile = new File("integration.txt");
				FileWriter fileW = new FileWriter(ofile,true);
				BufferedWriter out = new BufferedWriter(fileW);
				
				out.write(""+side_length+" "+(error/positiveQueryList.size())
						+" "+(time/positiveQueryList.size())+"\n");
				
				out.close();
			}
			catch(IOException e){}
			
			System.out.println("grid size: "+side_length
					+" average error: "+(error/positiveQueryList.size())
					+" time: "+(time/positiveQueryList.size()));
		}	
	}
}
*/