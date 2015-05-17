import java.util.ArrayList;
import java.util.HashMap;


public class LastHist3 {

	Hist2 intermediate;
	float side_length;
	double bar_width;
	
	public LastHist3(float size, double bar_width_input){
		
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
	}
	
	public void getSelf(Grid grid, Hist2 hist){
		
		
		Grid targetGrid = hist.getGrid(grid.x, grid.y);
		if(targetGrid != null){
			
			//如果tmpGrid和grid都是diagonal grid，特殊处理evaluation
			//否则，用bar统计数据
			if(targetGrid.x == targetGrid.y)
			{
				
				for(int i=0; i<targetGrid.dotList.size(); i++){
					if(isChildOfOneElementInList(grid.dotList, targetGrid.dotList.get(i))){
						targetGrid.dotList.get(i).isChild = true;
					}
				}
				this.intermediate.addGrid(targetGrid.x, targetGrid.y, targetGrid);

			}
			else
			{
							
				//发现如果右边或者下边全算的话，会引入大量的误差
				//用bar的least 和 largest 去refine估计
				//当target bar的least > query bar的largest时，
				//不用active target bar了
			
				for(int i = 0; i<grid.barList_X.length; i++){
					
					for(int j=i; j<targetGrid.barList_X.length; j++){
						
						if(grid.barList_X[i].largest_Y >= targetGrid.barList_X[j].least_Y ){
							
							targetGrid.barList_X[j].active = true;
						}
					}
				}

				this.intermediate.addGrid(targetGrid.x, targetGrid.y, targetGrid);
			}	
		}
	}

	public void getDown(Grid grid, Hist2 hist){
	
		Grid tmpGrid;
		
		HashMap<Integer, Grid> curCol = hist.cont.get(grid.x);

		if(curCol != null){
			
			ArrayList<Grid> curRowList = new ArrayList<Grid>(curCol.values());
			for(int i=0; i<curRowList.size(); i++){
				
				tmpGrid = curRowList.get(i);
				
				//包括了对角线位置的grid
				if(tmpGrid.y >= grid.x && tmpGrid.y < grid.y){
	
					for(int j=0; j<tmpGrid.barList_X.length; j++){	

						if(tmpGrid.barList_X[j].largest_X >= grid.least_x ){	
					
							tmpGrid.barList_X[j].active = true;							
						}
					}
														
					this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
				}
			}
		}	
	}
	
	
	public void getRight(Grid grid, Hist2 hist){
		
		Grid tmpGrid;

		//先处理非对角线上的grid
		for(int i=grid.x+1; i<=grid.y; i++){
			
			tmpGrid = hist.getGrid(i, grid.y);
			if(tmpGrid != null){
	
				for(int j=0; j<tmpGrid.barList_X.length; j++){
					
					if( grid.largest_y >= tmpGrid.barList_X[j].least_Y){

						tmpGrid.barList_X[j].active = true;						
					}			
				}
				this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
			}
		}
	}
	
	public void getDesc(Grid grid, Hist2 hist){
		
		Grid tmpGrid;
		//处于desc位置的grid有：grid.x+1<=tmpGrid.x<grid.y
		for(int i=grid.x+1; i<grid.y; i++){
			
			HashMap<Integer, Grid> curCol = hist.cont.get(i);
			if(curCol != null){
				
				ArrayList<Grid> curRowList = new ArrayList(curCol.values());
				for(int j=0; j<curRowList.size(); j++){
					
					tmpGrid = curRowList.get(j);
					//包括了对角线位置的grid
					if(tmpGrid.y >= i && tmpGrid.y < grid.y){
						for(int k=0; k<tmpGrid.barList_X.length; k++){
							tmpGrid.barList_X[k].active = true;
						}
						this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
					}
				}
			}
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
	
	public float getResult(){
		
		float total = 0;
		
		ArrayList<HashMap<Integer, Grid>> x_list2 = new ArrayList(this.intermediate.cont.values());
		for(int i=0; i<x_list2.size(); i++){
			
			ArrayList<Grid> y_list2 = new ArrayList(x_list2.get(i).values());
			for(int j=0; j<y_list2.size(); j++){
				
				Grid curGrid = y_list2.get(j);
				
				//有三套数据，X_bar_list, Y_bar_list, isChild, 找最多的那个
				float a1=0; float a3=0;
				
				for(int k=0; k<curGrid.barList_X.length; k++){
					
					if(curGrid.barList_X[k].active){
						a1 = a1 + curGrid.barList_X[k].cont.size();
					}		
				}
				
				for(int k=0; k<curGrid.dotList.size(); k++){
					
					if(curGrid.dotList.get(k).isChild){
						a3++;
					}
				}
				
				//找出最大的来				
				//float a5 = Math.max(a3, a1);

				float a5 = a3+a1;
				total = total + a5;
			}
		}
		
		//System.out.println("total is: "+total);
		
		return total;
	}
	
	public static void main(String[] args){
		
		int side_length = 500;
		double bar_width = 0.1;
		
		LastHist3 h = new LastHist3(side_length, bar_width);
		Hist2 h1 = new Hist2(side_length, bar_width);
		h1.loadHist("open_auction_queryNormal2.txt");
				
		Hist2 h2 = new Hist2(side_length, bar_width);
		h2.loadHist("incategory_targetNormal.txt");
		h.est(h1, h2);
		
		System.out.println("estimation: "+h.getResult());
		
	}
	
}
