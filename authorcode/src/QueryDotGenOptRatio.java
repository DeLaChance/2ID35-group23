import java.io.File;
import java.util.ArrayList;


/**
 * ����Ƚ�����query generation optimization���ж��ٸ�query dot����
 * ������optimization�ж��ٸ�query dot ����
 * ֻ����Ч��query����Ч��query����
 * @author ypeng
 *
 */
public class QueryDotGenOptRatio {
	
	public static void main(String[] args){
		
		
		
		int scaningfactor = 100;
		int start_of_third = 1496080;
		
		
		//String address = "D:/MyProject/SelectivityEst4/lab/";
		String address = "./lab/";
		
		//File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");	
		//File xmlFile = new File("auction.xml");
		File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
		MergingPro9 m = new MergingPro9(xmlFile, scaningfactor, start_of_third);
		m.merge();
		
		
		//����Ҫ����simple path�Ĳ���
		Gen2 g = new Gen2(2,2,DataVertex.labelToVertexIDMap,DataVertex.labelidmap);
		Query q;
		ArrayList<Query> positiveQueryList = new ArrayList();

		
		
		//����hist side_length=1���evaluation
		for(int i=0; i<1000; i++){ 
			
			int side_length = 1;
			double bar_width = 1;
			
			q = g.getQuery();
			System.out.println("\n"+q);
			
			m.output_query_position(q.labels.get(0), address);
			m.output_target_position(q.labels.get(1), address);
			
			float cur_t = 0;
			float cur_error = 0;
			
			//����evaluation
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
		
		//ǰ���Ϊ�˵õ�positiveQueryList
		//����������query dot gen�ķ�ʽ���Ż��ĺ�δ�Ż���ȥ����query dot
		{
			int side_length = 10;
			double bar_width = 0.1;
			
			
			int queryDotBeforeOpt = 0;
			int queryDotAfterOpt = 0;
			
			Query positiveQ;
			
			for(int i=0; i<positiveQueryList.size(); i++){
				
				positiveQ = positiveQueryList.get(i);
				System.out.println("\n"+positiveQ);
				
				//===========
				//����before opt��
				m.output_query_position(positiveQ.labels.get(0), address);
				m.output_target_position(positiveQ.labels.get(1), address);
				
				Hist2 queryCOM_beforeOpt = new Hist2(side_length, bar_width);
				queryCOM_beforeOpt.loadHist(address+""+positiveQ.labels.get(0)+"_queryCOM.txt");
				
				queryDotBeforeOpt += queryCOM_beforeOpt.get_dot_num();
				
				Hist2 queryNormal_beforeOpt = new Hist2(side_length, bar_width);
				queryNormal_beforeOpt.loadHist(address+""+positiveQ.labels.get(0)+"_queryNormal.txt");
				
				queryDotBeforeOpt += queryNormal_beforeOpt.get_dot_num();
				
				//===========
				//����after opt�ģ�before opt��ʱ���Ѿ�output��target�ˣ�����Ҫ�ã�
				m.output_query_position2(positiveQ.labels.get(0), positiveQ.labels.get(1), address);
				
				Hist2 queryCOM_afterOpt = new Hist2(side_length, bar_width);
				queryCOM_afterOpt.loadHist(address+""+positiveQ.labels.get(0)+"_queryCOM2.txt");
				
				queryDotAfterOpt += queryCOM_afterOpt.get_dot_num();
				
				Hist2 queryNormal_afterOpt =  new Hist2(side_length, bar_width);
				queryNormal_afterOpt.loadHist(address+""+positiveQ.labels.get(0)+"_queryNormal2.txt");
				
				queryDotAfterOpt += queryNormal_afterOpt.get_dot_num();
				
			}
			
			System.out.println("before opt: "+queryDotBeforeOpt);
			System.out.println("after opt: "+queryDotAfterOpt);
		
		}
		
		
	}
	
	
}
