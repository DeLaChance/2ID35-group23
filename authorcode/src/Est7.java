import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Est7 {

	
	public static void main(String[] args){
		
		int[] scanfactor = {100, 80, 70, 60, 50, 40, 30, 20, 10};
		int[] startOfThird = {1496080, 1201348, 1052521, 898589, 747755, 598055, 449841, 301231, 151179};
		
		//for(int z=0; z<4; z++){
		
		int scaningfactor = scanfactor[5];
		int start_of_third = startOfThird[5];
//		int scaningfactor = 1;
//		int start_of_third = 15444;
//		int AllCNodeNum = 106503;
		
		
//		String address = "D:/MyProject/SelectivityEst4/lab/";
		String address = "./lab/";
		
//		File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");	
		//File xmlFile = new File("auction.xml");
		File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
		MergingPro9 m = new MergingPro9(xmlFile, scaningfactor, start_of_third);
		m.merge();
		
		
		//现在要进行simple path的测试
		Gen2 g = new Gen2(2,2,DataVertex.labelToVertexIDMap,DataVertex.labelidmap);
		Query q;
		ArrayList<Query> positiveQueryList = new ArrayList();
		ArrayList<Query> largeErrorQueryList = new ArrayList();
		
/*		//这是check版的evaluation
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
		}*/
		
		
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
			
			//if(q.realValue > 50*scaningfactor){
			if(q.realValue > 0){
				positiveQueryList.add(q);
			}
		}
	
		
/*		//从文件中读1000个positiveQuery
		try{
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File("positiveQueryList"+scaningfactor+".txt")));
			
			String line = in.readLine();
			String[] tmp;
			
			//while(line != null){
			for(int i=0; i<500; i++){
				
				tmp = line.split(" ");
				Query newQ = new Query();
				newQ.labels.add(tmp[0]);
				newQ.labels.add(tmp[1]);
				newQ.realValue = Float.parseFloat(tmp[2]);
				positiveQueryList.add(newQ);
				
				line = in.readLine();
			}
			
		}
		catch(IOException e){ System.out.println(e); };
	*/	System.out.println("===============");
		
		//这是用bar统计技术的estimation
		for(int k=10; k<=300; k+=10)
		{	
			int side_length = k;
			double bar_width = 0.1;
			
			//这是estimation
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
		//		System.out.println(postiveQ);
				
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
		//		System.out.println("cur_error: "+cur_error);

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
		//		System.out.println("cur_error: "+cur_error);
				
				postiveQ.estTime = cur_t;
				postiveQ.estValue = cur_error;
				
				if( (Math.abs(postiveQ.estValue - postiveQ.realValue)/postiveQ.realValue) > 0.05 ){
					largeErrorQueryList.add(postiveQ);
				}
				
			}

			//把positiveQ们根据estValue进行排序
			Query[] tmpList = new Query[positiveQueryList.size()];
			positiveQueryList.toArray(tmpList);
			Arrays.sort(tmpList);
			int index = (int) (0.1*tmpList.length);
			float s = tmpList[index].estValue;
			
			System.out.println("tmpList length: "+tmpList.length);
			System.out.println("pos query list length: "+positiveQueryList.size());
			System.out.println("s: "+s);
			
			//System.out.println(positiveQueryList);
			double error = 0;
			double estTime = 0;
			double realTime = 0;
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				double tmp = Math.abs(postiveQ.estValue - postiveQ.realValue);
				//double tmp2 = tmp/postiveQ.realValue;
				double tmp2 = tmp/(Math.max(postiveQ.realValue,s));
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
		
		System.out.println(positiveQueryList.size());
		System.out.println(largeErrorQueryList);
	}
}
