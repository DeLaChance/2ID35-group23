import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Hist2 {


	HashMap<Integer, HashMap<Integer, Grid> > cont = new HashMap();
		
	int side_length;
	double bar_width;
	
	public Hist2(int abc, double bar_width_input){
	
		side_length = abc;
		bar_width = bar_width_input;
	}
		
	public void loadHist(String filename){
			
		try{
			File infile=new File(filename);
			FileReader fileReader = new FileReader(infile);
			BufferedReader in = new BufferedReader(fileReader);
				
			String line = in.readLine();
			String[] tmp;
				
			while(line != null){
					
				tmp = line.split(" ");
				float real_x = Integer.parseInt(tmp[0]); //这个是真正的x坐标
				float real_y = Integer.parseInt(tmp[1]); //这个是真正的y坐标
					
				//第二个版本的cast
				int cast_x = (int)Math.floor(real_x/side_length);
				int cast_y = (int)Math.floor(real_y/side_length);
					
				HashMap<Integer, Grid> hm = cont.get(cast_x);
					
				if(hm == null){
						
					HashMap<Integer, Grid> newhm = new HashMap();
						
					Grid grid = new Grid(side_length, bar_width);
					grid.x = cast_x;
					grid.y = cast_y;
					grid.add(real_x, real_y);
					
					newhm.put(cast_y, grid);
					cont.put(cast_x, newhm);					
				}
				else{
						
					Grid grid = hm.get(cast_y);
						
					if(grid == null){
						
						Grid newGrid = new Grid(side_length, bar_width);
						newGrid.x = cast_x;
						newGrid.y = cast_y;
						newGrid.add(real_x, real_y);
						
						hm.put(cast_y, newGrid);
					}
					else{
						
						grid.add(real_x, real_y);
					}
				}	
				
				line = in.readLine();
			}			
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
		
	public Grid getGrid(int x, int y){
		
		if(this.cont.get(x) == null){
			return null;
		}
		else if(this.cont.get(x).get(y) == null){
			return null;
		}
		else{
			return this.cont.get(x).get(y);
		}
	}
		
	public void addGrid(int x, int y, Grid grid){
		
		if(this.cont.get(x) == null){
			HashMap<Integer, Grid> hm = new HashMap();
			hm.put(y, grid);
			this.cont.put(x, hm);
		}
		else if(this.cont.get(x).get(y) == null){
			this.cont.get(x).put(y, grid);
		}
	}
	
	public void outputHist(){
		
		int diagonal_grid = 0;
		int undiagonal_grid = 0;
		
		ArrayList<HashMap<Integer, Grid>> x_list = new ArrayList(this.cont.values());
		
		for(int i=0; i<x_list.size(); i++){
			
			ArrayList<Grid> y_list = new ArrayList(x_list.get(i).values());
			for(int j=0; j<y_list.size(); j++){
				
				Grid curGrid = y_list.get(j);
				System.out.println("==========="+curGrid);
				//System.out.println(curGrid.dotList);
				//System.out.println("least_x is "+curGrid.least_x);
				//System.out.println("largest_y is "+curGrid.largest_y);
				
				if(curGrid.x == curGrid.y){
					diagonal_grid++;
				}
				else{
					undiagonal_grid++;
				}
				
				//输出bar统计数据
				for(int k=0; k<(1/bar_width); k++){
					System.out.print(curGrid.barList_Y[k].value+" ");
				}
				System.out.println();
			}
			
		}
		
		System.out.println("diagonal grid "+diagonal_grid);
		System.out.println("undiagonal grid "+undiagonal_grid);
	}
	
	//统计有多少个grid不为空，看看是不是线性关系
	public int get_grid_num(){
		
		int num = 0;
		
		ArrayList<HashMap<Integer, Grid>> x_list = new ArrayList(this.cont.values());
		
		for(int i=0; i<x_list.size(); i++){
			
			num = num + x_list.get(i).size();		
		}
		
		//System.out.println("non zero grid has "+num);
		return num;
	}
	
	//看看这个histogram中总共有多少个dot，所有的grid
	public int get_dot_num(){
		
		int total_dot_num = 0;
		
		ArrayList<HashMap<Integer, Grid>> x_list = new ArrayList(this.cont.values());
		
		for(int i=0; i<x_list.size(); i++){
			
			ArrayList<Grid> y_list = new ArrayList(x_list.get(i).values());
			for(int j=0; j<y_list.size(); j++){
				
				Grid curGrid = y_list.get(j);
				total_dot_num += curGrid.dotList.size();
			}
		}
		
		return total_dot_num;
	}
	
	public int get_diagonal_grid_num(){
		
		int total_diagonal_grid_num = 0;
		
		ArrayList<HashMap<Integer, Grid>> x_list = new ArrayList(this.cont.values());
		
		for(int i=0; i<x_list.size(); i++){
			
			ArrayList<Grid> y_list = new ArrayList(x_list.get(i).values());
			for(int j=0; j<y_list.size(); j++){
				
				Grid curGrid = y_list.get(j);
				if(curGrid.x == curGrid.y){
					total_diagonal_grid_num++;
				}
			}
		}
		
		return total_diagonal_grid_num;
	}
	
	//看看位于(x,x)的grid中，有多少个点是真在对角线上的
	public int[] countRealDiagonalDot(int x, int y){
		
		int[] abc = new int[2];
		abc[0] = 0;
		abc[1] = 0;
		
		Grid grid = this.getGrid(x, y);
		if(grid == null){
			
			return abc;
		}
		else{
			
			abc[0] = grid.dotList.size();
			
			//遍历一遍curGrid的dotList，看看多少个real diagonal dot
			for(int k=0; k<grid.dotList.size(); k++){
				
				if(grid.dotList.get(k).x == grid.dotList.get(k).y){
					abc[1]++;
				}
			}
			
			return abc;
		}
	}
		
	
	public static void main(String[] args){
		
		Hist2 histA = new Hist2(1, 1);
		histA.loadHist("bidder.txt");
		//histA.get_num_of_dots_in_Hist();
		histA.outputHist();
	}
}
