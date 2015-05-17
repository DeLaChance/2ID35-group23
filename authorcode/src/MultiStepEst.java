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
		
		//����Ҫ����simple path�Ĳ���
		Gen2 g = new Gen2(2,5,DataVertex.labelToVertexIDMap,DataVertex.labelidmap);
		Query q;
		ArrayList<Query> positiveQueryList = new ArrayList();
		
		for(int i=0; i<1000; i++){ 
			
			q = g.getQuery();
			System.out.println("\n"+q);
			
			float cur_r = 0; //���浱ǰquery��est result
			float cur_t = 0;
			
			//���ҳ�positive query����real value����
			//����֮��������Щpositive queryȥ����side_length����һ��
			
			int side_length = 1; //����Ϊ1����real value
			double bar_width = 1;
			
			//��query�ĵ�һ��step�������ļ�_queryCOM.txt,_queryNormal.txt 
			m.output_query_position(q.labels.get(0), address);
			
			//��query��ʣ�µ�step������_targetCOM.txt, _targetNormal.txt
			for(int j=1; j<q.labels.size(); j++){
				m.output_target_position(q.labels.get(j), address);
			}
		
			//���ھ�Ҫ��ʼest��,��intermediate hist������
			for(int j=0; j<q.labels.size()-1; j++){
				
				int cur_r_for_this_step = 0; //���浱ǰstep��est result
								
				//��load COM�ļ�
				Hist2 hist1COM = new Hist2(side_length, bar_width);
				Hist2 hist2COM = new Hist2(side_length, bar_width);

				if(j==0)
					hist1COM.loadHist(address+q.labels.get(j)+"_queryCOM.txt");
				else
					hist1COM.loadHist("inter_out_COM.txt");
				
				hist2COM.loadHist(address+q.labels.get(j+1)+"_targetCOM.txt");
				
				
				//��load Normal�ļ�
				Hist2 hist1Normal = new Hist2(side_length, bar_width);
				Hist2 hist2Normal = new Hist2(side_length, bar_width);
				
				if(j==0){
					hist1Normal.loadHist(address+q.labels.get(j)+"_queryNormal.txt");
				}
				else{
					hist1Normal.loadHist("inter_out_Normal.txt");
				}
				
				hist2Normal.loadHist(address+q.labels.get(j+1)+"_targetNormal.txt");
				

				//�ж��ǲ��ǵ���query�����һ��step��
				if(j+1 == q.labels.size()-1){//����xpath�����һ��step��
					
					//��COM
					LastHist3 lHistCOM = new LastHist3(side_length,bar_width);
					
					long time1 = System.nanoTime();
					lHistCOM.est(hist1COM, hist2COM);
					long time2 = System.nanoTime();
					
					cur_r += lHistCOM.getResult();	
					cur_t += (time2-time1);
					
					//��Normal
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
					
					//��COM
					InterHist iHistCOM = new InterHist(side_length, bar_width);
					
					long time5 = System.nanoTime();
					iHistCOM.est(hist1COM, hist2COM);
					long time6 = System.nanoTime();
					
					cur_r_for_this_step += iHistCOM.get_grid_num();
					cur_t += (time6-time5);
					
					//��Normal
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
					
					iHistCOM.genFileToGetDuplication("COM"); //����inter_in_COM.txt
					iHistNormal.genFileToGetDuplication("Normal"); //����inter_in_Normal.txt
					
					//����inter_out_COM.txt��inter_out_Normal.txt�ļ�
					m.output_inter_positionCOM();
					m.output_inter_positionNormal();
					
				}
			}
		}

		System.out.println("Real Value Computation Finished =========");
		System.out.println(positiveQueryList.size());
		
		//���ڿ�ʼest
		for(int k=10; k<=300; k+=10)
		{
			int side_length = k;
			double bar_width = 0.1;
			
			//����estimation
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				//System.out.println(postiveQ);
				
				float cur_t = 0;
				float cur_r = 0; //���ǵ�ǰquery ��estֵ
				
				
				//����ȥ�����ļ��ˣ�ǰ���Ѿ����ɹ���
				
				
				//���ھ�Ҫ��ʼest��,��intermediate hist������
				for(int j=0; j<postiveQ.labels.size()-1; j++){
					
					int cur_r_for_this_step = 0; //���浱ǰstep��est result
									
					//��load COM�ļ�
					Hist2 hist1COM = new Hist2(side_length, bar_width);
					Hist2 hist2COM = new Hist2(side_length, bar_width);

					if(j==0)
						hist1COM.loadHist(address+postiveQ.labels.get(j)+"_queryCOM.txt");
					else
						hist1COM.loadHist("inter_out_COM.txt");
					
					hist2COM.loadHist(address+postiveQ.labels.get(j+1)+"_targetCOM.txt");
					
					
					//��load Normal�ļ�
					Hist2 hist1Normal = new Hist2(side_length, bar_width);
					Hist2 hist2Normal = new Hist2(side_length, bar_width);
					
					if(j==0){
						hist1Normal.loadHist(address+postiveQ.labels.get(j)+"_queryNormal.txt");
					}
					else{
						hist1Normal.loadHist("inter_out_Normal.txt");
					}
					
					hist2Normal.loadHist(address+postiveQ.labels.get(j+1)+"_targetNormal.txt");
					

					//�ж��ǲ��ǵ���query�����һ��step��
					if(j+1 == postiveQ.labels.size()-1){//����xpath�����һ��step��
						
						//��COM
						LastHist3 lHistCOM = new LastHist3(side_length,bar_width);
						
						long time1 = System.nanoTime();
						lHistCOM.est(hist1COM, hist2COM);
						long time2 = System.nanoTime();
						
						cur_r += lHistCOM.getResult();	
						cur_t += (time2-time1);
						
						//��Normal
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
						
						//��COM
						InterHist iHistCOM = new InterHist(side_length, bar_width);
						
						long time5 = System.nanoTime();
						iHistCOM.est(hist1COM, hist2COM);
						long time6 = System.nanoTime();
						
						cur_r_for_this_step += iHistCOM.get_grid_num();
						cur_t += (time6-time5);
						
						//��Normal
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
						
						iHistCOM.genFileToGetDuplication("COM"); //����inter_in_COM.txt
						iHistNormal.genFileToGetDuplication("Normal"); //����inter_in_Normal.txt
						
						//����inter_out_COM.txt��inter_out_Normal.txt�ļ�
						m.output_inter_positionCOM();
						m.output_inter_positionNormal();
						
					}
				}
			}
			
			//���est�Ľ�����ļ���ȥ
			
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
