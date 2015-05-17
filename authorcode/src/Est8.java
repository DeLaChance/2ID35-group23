import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * 这个类用query dot generation opt后的query dot来搞，query dot的数目大大减少
 * 看看est 效果会不会有什么变化（发现：结果没多大差别）
 * @author ypeng
 *
 */
public class Est8 {

	
	public static void main(String[] args){
		
		int[] scanfactor = {80, 70, 60, 50, 40, 30, 20, 10};
		int[] startOfThird = {1201348, 1052521, 898589, 747755, 598055, 449841, 301231, 151179};
		
		//for(int z=0; z<4; z++){
		
		//int scaningfactor = scanfactor[5];
		//int start_of_third = startOfThird[5];
		int scaningfactor = 1;
		int start_of_third = 15444;
//		int AllCNodeNum = 106503;
		
		
		String address = "D:/MyProject/SelectivityEst4/lab/";
		//String address = "./lab/";
		
		File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");	
		//File xmlFile = new File("auction.xml");
		//File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
		MergingPro9 m = new MergingPro9(xmlFile, scaningfactor, start_of_third);
		m.merge();
		
		
		//现在要进行simple path的测试
		Gen2 g = new Gen2(2,2,DataVertex.labelToVertexIDMap,DataVertex.labelidmap);
		Query q;
		ArrayList<Query> positiveQueryList = new ArrayList();

		

		//这是hist side_length=1版的evaluation
		for(int i=0; i<1000; i++){ 
			
			int side_length = 1;
			double bar_width = 1;
			
			q = g.getQuery();
			System.out.println("\n"+q);
			
			m.output_query_position(q.labels.get(0), address);
			m.output_target_position(q.labels.get(1), address);
			
			float cur_t = 0;
			float cur_error = 0;
			
			//这是evaluation
			Hist2 queryHistCOM = new Hist2(side_length, bar_width);
			queryHistCOM.loadHist(address+""+q.labels.get(0)+"_queryCOM.txt");
			Hist2 targetHistCOM = new Hist2(side_length, bar_width);
			targetHistCOM.loadHist(address+""+q.labels.get(1)+"_targetCOM.txt");
			
			LastHist3 lastHistCOM = new LastHist3(side_length, bar_width);
			
			double time1 = System.nanoTime();
			lastHistCOM.est(queryHistCOM, targetHistCOM);
			double time2 = System.nanoTime();	
			
			cur_t += (time2-time1);
			cur_error += lastHistCOM.getResult();
			
			Hist2 queryHistNormal = new Hist2(side_length, bar_width);
			queryHistNormal.loadHist(address+""+q.labels.get(0)+"_queryNormal.txt");
			Hist2 targetHistNormal = new Hist2(side_length, bar_width);
			targetHistNormal.loadHist(address+""+q.labels.get(1)+"_targetNormal.txt");
			
			LastHist3 lastHistNormal = new LastHist3(side_length, bar_width);
			
			double time3 = System.nanoTime();
			lastHistNormal.est(queryHistNormal, targetHistNormal);
			double time4 = System.nanoTime();
			
			cur_t += (time4-time3);
			cur_error += lastHistNormal.getResult();
			
			q.realTime = cur_t;
			q.realValue = cur_error;
			
			if(q.realValue > 50*scaningfactor){
				positiveQueryList.add(q);
			}
		}
		
		System.out.println("===============");
		
		//这是用bar统计技术的estimation
		for(int k=10; k<=300; k+=10)
		{	
			int side_length = k;
			double bar_width = 0.1;
			
			//这是estimation
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				System.out.println(postiveQ);
				
				m.output_query_position2(postiveQ.labels.get(0), 
				                          postiveQ.labels.get(1), address);
				
				float cur_t = 0;
				float cur_error = 0;
				
				//==估计COM Histogram
				Hist2 hist1COM = new Hist2(side_length, bar_width);
				hist1COM.loadHist(address+""+postiveQ.labels.get(0)+"_queryCOM2.txt");
				Hist2 hist2COM = new Hist2(side_length, bar_width);
				hist2COM.loadHist(address+""+postiveQ.labels.get(1)+"_targetCOM.txt");
				
				
				LastHist3 lastHistCOM = new LastHist3(side_length, bar_width);
				double time1 = System.nanoTime();
				lastHistCOM.est(hist1COM, hist2COM);
				double time2 = System.nanoTime();				
				
				cur_t += (time2-time1);
				cur_error += lastHistCOM.getResult();
				System.out.println("cur_error: "+cur_error);

				//估计Normal Histogram
				Hist2 hist1Normal = new Hist2(side_length, bar_width);
				hist1Normal.loadHist(address+""+postiveQ.labels.get(0)+"_queryNormal2.txt");
				Hist2 hist2Normal = new Hist2(side_length, bar_width);
				hist2Normal.loadHist(address+""+postiveQ.labels.get(1)+"_targetNormal.txt");
				
				LastHist3 lastHistNormal = new LastHist3(side_length, bar_width);
				double time3 = System.nanoTime();
				lastHistNormal.est(hist1Normal, hist2Normal);
				double time4 = System.nanoTime();
				
				cur_t += (time2-time1);
				cur_error += lastHistNormal.getResult();
				System.out.println("cur_error: "+cur_error);
				
				postiveQ.estTime = cur_t;
				postiveQ.estValue = cur_error;
				
			}
			
			//System.out.println(positiveQueryList);
			double error = 0;
			double estTime = 0;
			double realTime = 0;
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				double tmp = Math.abs(postiveQ.estValue - postiveQ.realValue);
				double tmp2 = tmp/postiveQ.realValue;
				error = error + tmp2;			
				
				estTime = estTime + postiveQ.estTime;
				realTime = realTime + postiveQ.realTime;
			}
			
			try{
				File ofile = new File("integration_"+scaningfactor+".plt");
				FileWriter fileW = new FileWriter(ofile,true);
				BufferedWriter out = new BufferedWriter(fileW);
				
				out.write(""+side_length+" "+(error/positiveQueryList.size())
						+" "+(estTime/positiveQueryList.size())
						+" "+(realTime/positiveQueryList.size())+"\n");
				
				out.close();
			}
			catch(IOException e){}
			
			System.out.println("grid size: "+side_length
					+" average error: "+(error/positiveQueryList.size())
					+" estTime: "+(estTime/positiveQueryList.size())
					+" realTime: "+(realTime/positiveQueryList.size()));
		}
		
		/*{	
			int side_length = 300;
			double bar_width = 0.1;
			
			//这是estimation
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				System.out.println(postiveQ);
				
				float cur_t = 0;
				float cur_error = 0;
				
				//==估计COM Histogram
				Hist2 hist1COM = new Hist2(side_length, bar_width);
				hist1COM.loadHist(address+""+postiveQ.labels.get(0)+"_queryCOM.txt");
				Hist2 hist2COM = new Hist2(side_length, bar_width);
				hist2COM.loadHist(address+""+postiveQ.labels.get(1)+"_targetCOM.txt");
				
				
				LastHist3 lastHistCOM = new LastHist3(side_length, bar_width);
				double time1 = System.nanoTime();
				lastHistCOM.est(hist1COM, hist2COM);
				double time2 = System.nanoTime();				
				
				cur_t += (time2-time1);
				cur_error += lastHistCOM.getResult();
				System.out.println("cur_error: "+cur_error);

				//估计Normal Histogram
				Hist2 hist1Normal = new Hist2(side_length, bar_width);
				hist1Normal.loadHist(address+""+postiveQ.labels.get(0)+"_queryNormal.txt");
				Hist2 hist2Normal = new Hist2(side_length, bar_width);
				hist2Normal.loadHist(address+""+postiveQ.labels.get(1)+"_targetNormal.txt");
				
				LastHist3 lastHistNormal = new LastHist3(side_length, bar_width);
				double time3 = System.nanoTime();
				lastHistNormal.est(hist1Normal, hist2Normal);
				double time4 = System.nanoTime();
				
				cur_t += (time2-time1);
				cur_error += lastHistNormal.getResult();
				System.out.println("cur_error: "+cur_error);
				
				postiveQ.estTime = cur_t;
				postiveQ.estValue = cur_error;
				
			}
			
			//System.out.println(positiveQueryList);
			double error = 0;
			double estTime = 0;
			double realTime = 0;
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				double tmp = Math.abs(postiveQ.estValue - postiveQ.realValue);
				double tmp2 = tmp/postiveQ.realValue;
				error = error + tmp2;			
				
				estTime = estTime + postiveQ.estTime;
				realTime = realTime + postiveQ.realTime;
			}
			
			try{
				File ofile = new File("integration_"+scaningfactor+".plt");
				FileWriter fileW = new FileWriter(ofile,true);
				BufferedWriter out = new BufferedWriter(fileW);
				
				out.write(""+side_length+" "+(error/positiveQueryList.size())
						+" "+(estTime/positiveQueryList.size())
						+" "+(realTime/positiveQueryList.size())+"\n");
				
				out.close();
			}
			catch(IOException e){}
			
			System.out.println("grid size: "+side_length
					+" average error: "+(error/positiveQueryList.size())
					+" estTime: "+(estTime/positiveQueryList.size())
					+" realTime: "+(realTime/positiveQueryList.size()));
		}*/
		
		
	}
}
