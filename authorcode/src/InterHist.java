import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class InterHist {

	Hist2 intermediate;
	float side_length;
	double bar_width;
	
	public InterHist(float size, double bar_width_input){
		
		side_length = size;
		bar_width = bar_width_input;
	}
	
	public void est(Hist2 histA, Hist2 histB){
		
		intermediate = new Hist2(histA.side_length, bar_width);
		
		ArrayList<HashMap<Integer, Grid>> x_list = new ArrayList(histA.cont.values());
		
		for(int i=0; i<x_list.size(); i++){
			
			ArrayList<Grid> y_list = new ArrayList(x_list.get(i).values());
			
			for(int j=0; j<y_list.size(); j++){
				
				Grid curGrid = y_list.get(j);
				int x = curGrid.x;
				int y = curGrid.y;
				
				//getSelf
				getSelf(curGrid, histB);
				//getDown
				getDown(curGrid, histB);
				//getRight
				getRight(curGrid, histB);
				//getDesc
				getDesc(curGrid, histB);
				
			}
		}
		
		upDateLeastXandLargestY();
		
	}
	
	//看看这个histogram中有多少个非空的grid
	public int get_grid_num(){
		
		return this.intermediate.get_grid_num();
	}
	
	public void upDateLeastXandLargestY(){
		
		//遍历一遍inermediate，目的是更新各个grid的最小x和最大y，根据grid记录的x刀和y刀
		ArrayList<HashMap<Integer, Grid>> x_list2 = new ArrayList(this.intermediate.cont.values());
		
		for(int i=0; i<x_list2.size(); i++){
			
			ArrayList<Grid> y_list2 = new ArrayList(x_list2.get(i).values());
			for(int j=0; j<y_list2.size(); j++){
				
				Grid curGrid = y_list2.get(j);
				
				//如果curGrid.knif_x和curGrid.knif_y仍为初始值，
				//那么curGrid一定是对角线方格
				if(curGrid.least_x < curGrid.knif_x && curGrid.knif_x != Float.MAX_VALUE){				
					
					curGrid.least_x = curGrid.knif_x;
				}
				if(curGrid.largest_y > curGrid.knif_y && curGrid.knif_y != -1){
					
					curGrid.largest_y = curGrid.knif_y;
				}
			}
		}
	}
	
	public void getDown(Grid grid, Hist2 hist){
		
		Grid tmpGrid;
		//处于grid的down位置的tmpGrid有：tmpGrid.x=grid.x, grid.x<=tmpGrid.y<grid.y
		//且，当tmpGrid.y==grid.x时，tmpGrid处于对角线位置
				
		//先处理非对角线上的grid
		HashMap<Integer, Grid> curCol = hist.cont.get(grid.x);
		if(curCol != null){
			
			ArrayList<Grid> curRowList = new ArrayList(curCol.values());
			for(int i=0; i<curRowList.size(); i++){
				
				tmpGrid = curRowList.get(i);
				if(tmpGrid.y > grid.x && tmpGrid.y < grid.y){
					tmpGrid.knif_x = Math.min(tmpGrid.knif_x, grid.least_x);
					this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
				}
			}
		}
				
		//再处理对角线上的grid, 用evaluation
		tmpGrid = hist.getGrid(grid.x, grid.x);
		if(tmpGrid != null){
			
			for(int i=0; i<tmpGrid.dotList.size(); i++){
				if(isChildOfOneElementInList(grid.dotList, tmpGrid.dotList.get(i))){
					tmpGrid.dotList.get(i).isChild = true;
				}
			}
			this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
		}		
	}
	
	
	public void getRight(Grid grid, Hist2 hist){
		
		Grid tmpGrid;
		//处于grid的right位置上的grid有：tmpGrid.y=grid.y; grid.x<tmpGrid.x<=grid.y
		//且，当tmpGrid.x==grid.y时，tmpGrid处于对角线位置
		
		//先处理非对角线上的grid
		for(int i=grid.x+1; i<grid.y; i++){
			
			tmpGrid = hist.getGrid(i, grid.y);
			if(tmpGrid != null){
				
				tmpGrid.knif_y = Math.max(tmpGrid.knif_y, grid.largest_y);
				this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
			}			
		}
		
		//再处理对角线上的grid， 用evaluation
		tmpGrid = hist.getGrid(grid.y, grid.y);
		if(tmpGrid != null){
			
			for(int i=0; i<tmpGrid.dotList.size(); i++){

				if(isChildOfOneElementInList(grid.dotList, tmpGrid.dotList.get(i))){
					tmpGrid.dotList.get(i).isChild = true;
				}
			}
			this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
		}
	}
	
	
	public void getDesc(Grid grid, Hist2 hist){
		
		Grid tmpGrid;
		//处于desc位置的grid有:(x 坐标) grid.x+1<=tmpGrid.x<grid.y
		for(int i=grid.x+1; i<grid.y; i++){
			
			//也要区分对角线位置
			//先处理非对角线位置
			HashMap<Integer, Grid> curCol = hist.cont.get(i);
			if(curCol != null){
				
				ArrayList<Grid> curRowList = new ArrayList(curCol.values());
				for(int j=0; j<curRowList.size(); j++){
					
					tmpGrid = curRowList.get(j);
					if(tmpGrid.y > i && tmpGrid.y < grid.y){
						tmpGrid.knif_x = tmpGrid.least_x;
						tmpGrid.knif_y = tmpGrid.largest_y;
						this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
					}
				}
			}
			
			//再处理对角线位置,dotList的元素的isChild全置为true
			tmpGrid = hist.getGrid(i, i);
			if(tmpGrid != null){
				for(int j=0; j<tmpGrid.dotList.size(); j++){
					tmpGrid.dotList.get(j).isChild = true;
				}
				this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
			}
		}
	}
	
	
	public void getSelf(Grid grid, Hist2 hist){
		
		Grid tmpGrid = hist.getGrid(grid.x, grid.y);
		
		if(tmpGrid != null){
						
			if(tmpGrid.x == tmpGrid.y){
				
				//如果grid是对角线位置，要evaluation
				for(int i=0; i<tmpGrid.dotList.size(); i++){
					if(isChildOfOneElementInList(grid.dotList, tmpGrid.dotList.get(i))){
						tmpGrid.dotList.get(i).isChild = true;
					}
				}
				
			}
			else{
				
				//如果grid非对角线位置，要设置knif_x和knif_y
				tmpGrid.knif_x = Math.min(tmpGrid.knif_x, grid.least_x);
				tmpGrid.knif_y = Math.max(tmpGrid.knif_y, grid.largest_y);
			}
			
			this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
		}		
	}
	
	
	//判断B是不是list中某一个的child
	public boolean isChildOfOneElementInList(ArrayList<Position> list, Position B){
		
		for(int i=0; i<list.size(); i++){
			
			if( isChild(list.get(i), B) ){
				return true;
			}
		}
		return false;
	}
	// check whether B is a child of A
	public boolean isChild(Position A, Position B){
		
		if((A.x <= B.x) && (A.y >= B.y)){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public void genFileToGetDuplication(String COM_or_Normal){
				
		try{
			
			File outfile = new File("inter_in_"+COM_or_Normal+".txt");
			FileWriter fileW = new FileWriter(outfile);
			BufferedWriter out = new BufferedWriter(fileW);
			
			ArrayList<HashMap<Integer, Grid>> x_list = new ArrayList(intermediate.cont.values());
			
			for(int i=0; i<x_list.size(); i++){
				
				ArrayList<Grid> y_list = new ArrayList(x_list.get(i).values());
				
				for(int j=0; j<y_list.size(); j++){
					
					Grid curGrid = y_list.get(j);
					//对角线上的方格，不能用最小x和最大y来表示
					if(curGrid.x == curGrid.y){
						for(int k=0; k<curGrid.dotList.size(); k++){
							//只输出isChild=true的
							if(curGrid.dotList.get(k).isChild){
								out.write((int)curGrid.dotList.get(k).x 
										+" "+(int)curGrid.dotList.get(k).y +"\n");
							}
						}
					}
					else{
						out.write((int)curGrid.least_x +" "+(int)curGrid.largest_y+"\n");					
					}
				}
				
			}
				
			out.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}

}
