import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class MultiStepEst {

	public static void main(String[] args){
		
		int[] scanfactor = {100, 80, 70, 60, 50, 40, 30, 20, 10};
		int[] startOfThird = {1496080, 1201348, 1052521, 898589, 747755, 598055, 449841, 301231, 151179};
		
		
		int scaningfactor = scanfactor[5];
		int start_of_third = startOfThird[5];
		
//		int scaningfactor = 1;
//		int start_of_third = 15444;
		
//		String address = "D:/MyProject/SelectivityEst4/lab/";
		String address = "./lab/";
		
//		File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");	
		//File xmlFile = new File("auction.xml");
		File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
		
		MergingPro9 m = new MergingPro9(xmlFile, scaningfactor, start_of_third);
		m.merge();
		
		//现在要进行simple path的测试
		Gen2 g = new Gen2(2,5,DataVertex.labelToVertexIDMap,DataVertex.labelidmap);
		Query q;
		ArrayList<Query> positiveQueryList = new ArrayList();
		
		for(int i=0; i<1000; i++){ 
			
			q = g.getQuery();
			System.out.println("\n"+q);
			
			float cur_r = 0; //保存当前query的est result
			float cur_t = 0;
			
			//先找出positive query及其real value来，
			//完了之后再用这些positive query去增大side_length再做一遍
			
			int side_length = 1; //设置为1就是real value
			double bar_width = 1;
			
			//对query的第一个step，生成文件_queryCOM.txt,_queryNormal.txt 
			m.output_query_position(q.labels.get(0), address);
			
			//对query的剩下的step，生成_targetCOM.txt, _targetNormal.txt
			for(int j=1; j<q.labels.size(); j++){
				m.output_target_position(q.labels.get(j), address);
			}
		
			//现在就要开始est了,用intermediate hist就行了
			for(int j=0; j<q.labels.size()-1; j++){
				
				int cur_r_for_this_step = 0; //保存当前step的est result
								
				//先load COM文件
				Hist2 hist1COM = new Hist2(side_length, bar_width);
				Hist2 hist2COM = new Hist2(side_length, bar_width);

				if(j==0)
					hist1COM.loadHist(address+q.labels.get(j)+"_queryCOM.txt");
				else
					hist1COM.loadHist("inter_out_COM.txt");
				
				hist2COM.loadHist(address+q.labels.get(j+1)+"_targetCOM.txt");
				
				
				//再load Normal文件
				Hist2 hist1Normal = new Hist2(side_length, bar_width);
				Hist2 hist2Normal = new Hist2(side_length, bar_width);
				
				if(j==0){
					hist1Normal.loadHist(address+q.labels.get(j)+"_queryNormal.txt");
				}
				else{
					hist1Normal.loadHist("inter_out_Normal.txt");
				}
				
				hist2Normal.loadHist(address+q.labels.get(j+1)+"_targetNormal.txt");
				

				//判断是不是到了query的最后一个step了
				if(j+1 == q.labels.size()-1){//到了xpath的最后一个step了
					
					//先COM
					LastHist3 lHistCOM = new LastHist3(side_length,bar_width);
					
					long time1 = System.nanoTime();
					lHistCOM.est(hist1COM, hist2COM);
					long time2 = System.nanoTime();
					
					cur_r += lHistCOM.getResult();	
					cur_t += (time2-time1);
					
					//再Normal
					LastHist3 lHistNormal = new LastHist3(side_length, bar_width);
					
					long time3 = System.nanoTime();
					lHistNormal.est(hist1Normal, hist2Normal);
					long time4 = System.nanoTime();
					
					cur_r += lHistNormal.getResult();
					cur_t += (time4-time3);
					
					if(cur_r > 50*scaningfactor){
						
						q.realValue = cur_r;
						q.realTime = cur_t;
						positiveQueryList.add(q);
					}
				}
				else{
					
					//先COM
					InterHist iHistCOM = new InterHist(side_length, bar_width);
					
					long time5 = System.nanoTime();
					iHistCOM.est(hist1COM, hist2COM);
					long time6 = System.nanoTime();
					
					cur_r_for_this_step += iHistCOM.get_grid_num();
					cur_t += (time6-time5);
					
					//再Normal
					InterHist iHistNormal = new InterHist(side_length, bar_width);
					
					long time7 = System.nanoTime();
					iHistNormal.est(hist1Normal, hist2Normal);
					long time8 = System.nanoTime();
					
					cur_r_for_this_step += iHistNormal.get_grid_num();
					cur_t += (time8-time7);
					
					if(cur_r_for_this_step == 0){
						
						System.out.println("iHist Is Empty!!!========"+i);
						break;
					}
					
					iHistCOM.genFileToGetDuplication("COM"); //生成inter_in_COM.txt
					iHistNormal.genFileToGetDuplication("Normal"); //生成inter_in_Normal.txt
					
					//生成inter_out_COM.txt和inter_out_Normal.txt文件
					m.output_inter_positionCOM();
					m.output_inter_positionNormal();
					
				}
			}
		}

		System.out.println("Real Value Computation Finished =========");
		System.out.println(positiveQueryList.size());
		
		//现在开始est
		for(int k=10; k<=300; k+=10)
		{
			int side_length = k;
			double bar_width = 0.1;
			
			//这是estimation
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				//System.out.println(postiveQ);
				
				float cur_t = 0;
				float cur_r = 0; //这是当前query 的est值
				
				
				//不用去生成文件了，前面已经生成过了
				
				
				//现在就要开始est了,用intermediate hist就行了
				for(int j=0; j<postiveQ.labels.size()-1; j++){
					
					int cur_r_for_this_step = 0; //保存当前step的est result
									
					//先load COM文件
					Hist2 hist1COM = new Hist2(side_length, bar_width);
					Hist2 hist2COM = new Hist2(side_length, bar_width);

					if(j==0)
						hist1COM.loadHist(address+postiveQ.labels.get(j)+"_queryCOM.txt");
					else
						hist1COM.loadHist("inter_out_COM.txt");
					
					hist2COM.loadHist(address+postiveQ.labels.get(j+1)+"_targetCOM.txt");
					
					
					//再load Normal文件
					Hist2 hist1Normal = new Hist2(side_length, bar_width);
					Hist2 hist2Normal = new Hist2(side_length, bar_width);
					
					if(j==0){
						hist1Normal.loadHist(address+postiveQ.labels.get(j)+"_queryNormal.txt");
					}
					else{
						hist1Normal.loadHist("inter_out_Normal.txt");
					}
					
					hist2Normal.loadHist(address+postiveQ.labels.get(j+1)+"_targetNormal.txt");
					

					//判断是不是到了query的最后一个step了
					if(j+1 == postiveQ.labels.size()-1){//到了xpath的最后一个step了
						
						//先COM
						LastHist3 lHistCOM = new LastHist3(side_length,bar_width);
						
						long time1 = System.nanoTime();
						lHistCOM.est(hist1COM, hist2COM);
						long time2 = System.nanoTime();
						
						cur_r += lHistCOM.getResult();	
						cur_t += (time2-time1);
						
						//再Normal
						LastHist3 lHistNormal = new LastHist3(side_length, bar_width);
						
						long time3 = System.nanoTime();
						lHistNormal.est(hist1Normal, hist2Normal);
						long time4 = System.nanoTime();
						
						cur_r += lHistNormal.getResult();
						cur_t += (time4-time3);
							
						postiveQ.estValue = cur_r;	
						postiveQ.estTime = cur_t;

					}
					else{
						
						//先COM
						InterHist iHistCOM = new InterHist(side_length, bar_width);
						
						long time5 = System.nanoTime();
						iHistCOM.est(hist1COM, hist2COM);
						long time6 = System.nanoTime();
						
						cur_r_for_this_step += iHistCOM.get_grid_num();
						cur_t += (time6-time5);
						
						//再Normal
						InterHist iHistNormal = new InterHist(side_length, bar_width);
						
						long time7 = System.nanoTime();
						iHistNormal.est(hist1Normal, hist2Normal);
						long time8 = System.nanoTime();
						
						cur_r_for_this_step += iHistNormal.get_grid_num();
						cur_t += (time8-time7);
						
						if(cur_r_for_this_step == 0){
							
							System.out.println("iHist Is Empty!!!========"+i);
							break;
						}
						
						iHistCOM.genFileToGetDuplication("COM"); //生成inter_in_COM.txt
						iHistNormal.genFileToGetDuplication("Normal"); //生成inter_in_Normal.txt
						
						//生成inter_out_COM.txt和inter_out_Normal.txt文件
						m.output_inter_positionCOM();
						m.output_inter_positionNormal();
						
					}
				}
			}
			
			//输出est的结果到文件中去
			
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
				File ofile = new File("integration_"+scaningfactor+"_multistep.plt");
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
		
		System.out.println("positive query num: "+positiveQueryList);
	}
}
