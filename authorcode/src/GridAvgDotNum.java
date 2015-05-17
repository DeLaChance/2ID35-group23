import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 这个是用来看平均每个grid中含有多少个dot的
 * @author ypeng
 *
 */
public class GridAvgDotNum {

	public static void main(String[] args){
			
		int scaningfactor = 100;
		int start_of_third = 1496080;
		//int scaningfactor = 1;
		//int start_of_third = 15444;
		
		int side_length = 500;
		double bar_width = 0.1;
		
		//String address = "D:/MyProject/SelectivityEst4/lab2/";
		String address = "./lab/";
		
		//File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");	
		//File xmlFile = new File("auction.xml");
		File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
		MergingPro9 m = new MergingPro9(xmlFile, scaningfactor, start_of_third);
		m.merge();
		
		ArrayList<String> labelList = new ArrayList(DataVertex.labelidmap.keySet());
		
		for(int i=0; i<labelList.size(); i++){
			
			m.output_target_position(labelList.get(i), address);
			
		}
		Hist2 histCOM = new Hist2(side_length, bar_width);
		Hist2 histNormal = new Hist2(side_length, bar_width);
		
		for(int i=0; i<labelList.size(); i++){
			
			histCOM.loadHist(address+labelList.get(i)+"_targetCOM.txt");
			histNormal.loadHist(address+labelList.get(i)+"_targetNormal.txt");
		}
		
		double grid_num_COM = histCOM.get_grid_num();
		int dot_num_COM = histCOM.get_dot_num();
		
		double grid_num_Normal = histNormal.get_grid_num();
		int dot_num_Normal = histNormal.get_dot_num();
		
		System.out.println("grid num_COM: "+grid_num_COM);
		System.out.println("dot num_COM: "+dot_num_COM);
		System.out.println("raio_COM: "+(dot_num_COM/grid_num_COM));
		
		System.out.println("grid num_Normal: "+grid_num_Normal);
		System.out.println("dot num_Normal: "+dot_num_Normal);
		System.out.println("raio_Normal: "+(dot_num_Normal/grid_num_Normal));
		
		double grid_num_total = grid_num_COM + grid_num_Normal;
		int dot_num_total = dot_num_COM + dot_num_Normal;
		
		System.out.println("grid num_total: "+grid_num_total);
		System.out.println("dot num_total: "+dot_num_total);
		System.out.println("raio_total: "+(dot_num_total/grid_num_total));

		try{
			BufferedWriter out = new BufferedWriter(
					new FileWriter(
					new File("GridAvgDotNum_"+scaningfactor+".txt")));
			
			
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
}
