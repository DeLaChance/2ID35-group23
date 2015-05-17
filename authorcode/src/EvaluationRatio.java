/*
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

*//**
 * 这个类用来输出不同的grid size 下，对query a//b来讲，被真正evaluate的b grid
 * 占总共b grid的数目的比例
 * @author ypeng
 *
 *//*

public class EvaluationRatio {


	
	public static void main(String[] args){
		
		int[] scanfactor = {100, 80, 70, 60, 50, 40, 20};
		int[] startOfThird = {1496080, 1201348, 1052521, 898589, 747755, 598055, 301231};
		
		//for(int z=0; z<4; z++){
		
		int scaningfactor = scanfactor[0];
		int start_of_third = startOfThird[0];
		//int scaningfactor = 100;
		//int start_of_third = 1496080;
		//int scaningfactor = 1;
		//int start_of_third = 15444;
		
		
		//String address = "D:/MyProject/SelectivityEst4/lab/";
		String address = "../lab/";
		
		//File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");	
		//File xmlFile = new File("auction.xml");
		File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
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
		
		int[] abcde = {10, 50, 100, 200, 300};
		//这是用bar统计技术的estimation
		for(int k=0; k<5; k++)
		{	
			int side_length = abcde[k];
			double bar_width = 0.1;
			
			int evaluation_grids_num = 0;
			int total_grids_num = 0;
			int dot_at_diagonal_q = 0;
			int dot_at_diagonal_t = 0;
			int real_diagonal_dot_q = 0;
			int real_diagonal_dot_t = 0;
			int total_dot_num = 0;
			//target hist中需要evaluation的grid中多少真正需要evaluation的bar
			int bar_need_evaluation_in_evaluation_grid = 0; 
			//target hist中需要evaluation的grid中总共多少个bar
			int total_bar_in_evaluation_grid = 0;
			
			//bar加上了least和largest值之后，真正需要evaluation的bar数目
			int bar_need_evaluation_with_least_largest_in_bar = 0;
			
			int bar_need_evaluation_in_evaluation_grid_Y = 0; 
			int total_bar_in_evaluation_grid_Y = 0;
			
			//这是estimation
			for(int i=0; i<positiveQueryList.size(); i++){
				
				Query postiveQ = positiveQueryList.get(i);
				
				m.output_query_position2(postiveQ.labels.get(0), 
                        postiveQ.labels.get(1), address);

				
				float cur_t = 0;
				float cur_error = 0;
				
				//==估计COM Histogram
				Hist2 hist1COM = new Hist2(side_length, bar_width);
				hist1COM.loadHist(address+""+postiveQ.labels.get(0)+"_queryCOM.txt");
				Hist2 hist2COM = new Hist2(side_length, bar_width);
				hist2COM.loadHist(address+""+postiveQ.labels.get(1)+"_targetCOM.txt");
				
				total_grids_num += hist2COM.get_grid_num();
				total_dot_num += hist2COM.get_dot_num();
				
				LastHist3 lastHistCOM = new LastHist3(side_length, bar_width);
				double time1 = System.nanoTime();
				lastHistCOM.est(hist1COM, hist2COM);
				double time2 = System.nanoTime();				
				
				cur_t += (time2-time1);
				cur_error += lastHistCOM.getResult();
				
				evaluation_grids_num += lastHistCOM.num_of_target_grids_need_evaluation;
				dot_at_diagonal_q += lastHistCOM.dot_at_diagonal_grid_q;
				dot_at_diagonal_t += lastHistCOM.dot_at_diagonal_grid_t;
				real_diagonal_dot_q += lastHistCOM.real_diagonal_dot_q;
				real_diagonal_dot_t += lastHistCOM.real_diagonal_dot_t;
				bar_need_evaluation_in_evaluation_grid += lastHistCOM.num_of_bars_need_evaluation_in_evaluated_target_grid;
				total_bar_in_evaluation_grid += lastHistCOM.num_of_total_bars_in_evaluated_target_grid;
				bar_need_evaluation_in_evaluation_grid_Y += lastHistCOM.num_of_bars_need_evaluation_in_evaluated_target_grid_Y;
				total_bar_in_evaluation_grid_Y += lastHistCOM.num_of_total_bars_in_evaluated_target_grid_Y;
				bar_need_evaluation_with_least_largest_in_bar += lastHistCOM.num_of_bars_need_evaluation_with_least_largest_in_bar;
				
				//估计Normal Histogram
				Hist2 hist1Normal = new Hist2(side_length, bar_width);
				hist1Normal.loadHist(address+""+postiveQ.labels.get(0)+"_queryNormal.txt");
				Hist2 hist2Normal = new Hist2(side_length, bar_width);
				hist2Normal.loadHist(address+""+postiveQ.labels.get(1)+"_targetNormal.txt");
				
				total_grids_num += hist2Normal.get_grid_num();
				total_dot_num += hist2Normal.get_dot_num();
				
				LastHist3 lastHistNormal = new LastHist3(side_length, bar_width);
				double time3 = System.nanoTime();
				lastHistNormal.est(hist1Normal, hist2Normal);
				double time4 = System.nanoTime();
				
				cur_t += (time2-time1);
				cur_error += lastHistNormal.getResult();
				
				evaluation_grids_num += lastHistNormal.num_of_target_grids_need_evaluation;
				dot_at_diagonal_q += lastHistNormal.dot_at_diagonal_grid_q;
				dot_at_diagonal_t += lastHistNormal.dot_at_diagonal_grid_t;
				real_diagonal_dot_q += lastHistNormal.real_diagonal_dot_q;
				real_diagonal_dot_t += lastHistNormal.real_diagonal_dot_t;
				bar_need_evaluation_in_evaluation_grid += lastHistNormal.num_of_bars_need_evaluation_in_evaluated_target_grid;
				total_bar_in_evaluation_grid += lastHistNormal.num_of_total_bars_in_evaluated_target_grid;
				bar_need_evaluation_in_evaluation_grid_Y += lastHistNormal.num_of_bars_need_evaluation_in_evaluated_target_grid_Y;
				total_bar_in_evaluation_grid_Y += lastHistNormal.num_of_total_bars_in_evaluated_target_grid_Y;
				bar_need_evaluation_with_least_largest_in_bar += lastHistNormal.num_of_bars_need_evaluation_with_least_largest_in_bar;
		
			
			}
			
			try{
				File ofile = new File("evaluation_ratio_"+scaningfactor+".plt");
				FileWriter fileW = new FileWriter(ofile,true);
				BufferedWriter out = new BufferedWriter(fileW);
				
				out.write(""+abcde[k]+"=============\n");
				out.write("total 'b' grid: " +total_grids_num+"\n");
				out.write("evaluation 'b' grid: "+evaluation_grids_num+"\n");
				out.write("evaluation ratio: "+(((double)(evaluation_grids_num))/total_grids_num)+"\n");				
				out.write("X bar need evlu: "+bar_need_evaluation_in_evaluation_grid+"\n");
				out.write("total X bar: "+total_bar_in_evaluation_grid+"\n");
				out.write("Y bar need evlu: "+bar_need_evaluation_in_evaluation_grid_Y+"\n");
				out.write("total Y bar: "+total_bar_in_evaluation_grid_Y+"\n");
				out.write("X bar need evlu with least&largest in bar: "+bar_need_evaluation_with_least_largest_in_bar+"\n");
				//out.write("dot_at_diagonal_q: "+dot_at_diagonal_q+"\n");
				//out.write("dot_at_diagonal_t: "+dot_at_diagonal_t+"\n");
				//out.write("real_diagonal_dot_q: "+real_diagonal_dot_q+"\n");
				//out.write("real_diagonal_dot_t: "+real_diagonal_dot_t+"\n");				
				
				
				out.close();
			}
			catch(IOException e){}
			
			System.out.println(abcde[k]+"+++++++++++++++++++++++++++++");
			System.out.println("total 'b' grid: " +total_grids_num);
			System.out.println("evaluation 'b' grid: "+evaluation_grids_num);
			System.out.println("evaluation ratio: "+(((double)(evaluation_grids_num))/total_grids_num));
			System.out.println("dot_at_diagonal_q: "+dot_at_diagonal_q);
			System.out.println("dot_at_diagonal_t: "+dot_at_diagonal_t);
			System.out.println("real_diagonal_dot_q: "+real_diagonal_dot_q);
			System.out.println("real_diagonal_dot_t: "+real_diagonal_dot_t);
			System.out.println("total 'b' dot: "+total_dot_num);
			System.out.println("grid avg dot num: "+(((double)(total_dot_num))/total_grids_num));
		}
	}
}
*/