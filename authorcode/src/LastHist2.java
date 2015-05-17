import java.util.ArrayList;
import java.util.HashMap;

/**
 * 这个类是用bar统计数据进行估计的
 * @author ypeng
 *
 */
public class LastHist2 {

	Hist2 intermediate;
	float side_length;
	double bar_width;
	
	int num_of_target_grids_need_evaluation = 0;
	//在需要evaluation的target grid中，有多少个bar是真正需要evaluation的
	int num_of_bars_need_evaluation_in_evaluated_target_grid = 0;
	int num_of_total_bars_in_evaluated_target_grid = 0;
	
	//每个bar加上最小最大值之后，看看有多少个真正需要evaluation的bar
	int num_of_bars_need_evaluation_with_least_largest_in_bar = 0;
	
	int num_of_bars_need_evaluation_in_evaluated_target_grid_Y = 0;
	int num_of_total_bars_in_evaluated_target_grid_Y = 0;
	
	int dot_at_diagonal_grid_q = 0; 
	int real_diagonal_dot_q = 0;
	int dot_at_diagonal_grid_t = 0;
	int real_diagonal_dot_t = 0;
	Hist2 queryHist;
	Hist2 targetHist;
	
	public LastHist2(float size, double bar_width_input){
		
		side_length = size;
		bar_width = bar_width_input;
	}
	
	public void est(Hist2 histA, Hist2 histB){
		
		queryHist = histA; targetHist = histB;
		
		
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
			if(targetGrid.x == targetGrid.y){
				
				for(int i=0; i<targetGrid.dotList.size(); i++){
					if(isChildOfOneElementInList(grid.dotList, targetGrid.dotList.get(i))){
						targetGrid.dotList.get(i).isChild = true;
					}
				}
				this.intermediate.addGrid(targetGrid.x, targetGrid.y, targetGrid);
				
				num_of_target_grids_need_evaluation++;
				
				int[] q = queryHist.countRealDiagonalDot(grid.x, grid.y);
				int[] t = targetHist.countRealDiagonalDot(grid.x, grid.y);
				
				dot_at_diagonal_grid_q += q[0];
				dot_at_diagonal_grid_t += t[0];
				real_diagonal_dot_q += q[1];
				real_diagonal_dot_t += t[1];
				
				//先把query grid的最左边的不为空的bar的位置找出来
				int pos = grid.getLeftMostBar();
				//看看target grid中有多少个真正需要evaluation的bar
				num_of_bars_need_evaluation_in_evaluated_target_grid+=targetGrid.getBarNum_X(pos);
				//看看target grid中总共有多少个bar
				num_of_total_bars_in_evaluated_target_grid+=targetGrid.getBarNum_X();
				
				//看看bar加上least和largest值之后，有多少个需要evaluation的bar
				num_of_bars_need_evaluation_with_least_largest_in_bar
							+= targetGrid.getBarNum_X(grid.getBar_X(pos), pos);
				
				int pos_Y = grid.getUpMostBar();
				num_of_bars_need_evaluation_in_evaluated_target_grid_Y += targetGrid.getBarNum_Y(pos_Y);
				num_of_total_bars_in_evaluated_target_grid_Y += targetGrid.getBarNum_Y();
			}
			else{
				
				int X_bar_index = ((int)(grid.least_x - grid.x*side_length))/(int)(side_length*bar_width);
				
				//发现如果右边或者下边全算的话，会引入大量的误差
				//用bar的least 和 largest 去refine估计
				//当target bar的least > query bar的largest时，
				//不用active target bar了

				
				for(int i = 0; i<grid.barList_X.length; i++){
					
					for(int j=i+1; j<grid.barList_X.length; j++){
						
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
		
		int X_bar_index = ((int)(grid.least_x - grid.x*side_length))/(int)(side_length*bar_width);
		
		HashMap<Integer, Grid> curCol = hist.cont.get(grid.x);
		if(curCol != null){
			
			ArrayList<Grid> curRowList = new ArrayList(curCol.values());
			for(int i=0; i<curRowList.size(); i++){
				
				tmpGrid = curRowList.get(i);
				//包括了对角线位置的grid
				if(tmpGrid.y >= grid.x && tmpGrid.y < grid.y){

					//for(int j=X_bar_index; j<tmpGrid.barList_X.length; j++){
					for(int j=0; j<tmpGrid.barList_X.length; j++){	
						//if( !(tmpGrid.barList_X[j].largest_X < grid.least_x)){
						if(tmpGrid.barList_X[j].largest_X >= grid.least_x ){	
						
							/*if( tmpGrid.barList_X[j].least_X < grid.least_x){
								
								tmpGrid.barList_X[j].half_active = true;	
								
								float abc = (tmpGrid.barList_X[j].largest_X - grid.least_x + 1);
								float abc2 = abc/(tmpGrid.barList_X[j].largest_X-tmpGrid.barList_X[j].least_X + 1);
								if(abc2 > tmpGrid.barList_X[j].covered_bar_area){
									tmpGrid.barList_X[j].covered_bar_area = abc2;
								}
							}
							else{*/
								tmpGrid.barList_X[j].active = true;	
							//}
													
						}
						//tmpGrid.barList_X[j].active = true;	
					}
														
					this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
				}
			}
		}	
	}
	
	public void getRight(Grid grid, Hist2 hist){
		
		Grid tmpGrid;
		
		int Y_bar_index = ((int)(grid.largest_y - grid.y*side_length))/(int)(side_length*bar_width);
		
		int X_bar_index = ((int)(grid.least_x - grid.x*side_length))/(int)(side_length*bar_width);
		Bar querybar_X = grid.barList_X[X_bar_index];
		
		//先处理非对角线上的grid
		for(int i=grid.x+1; i<=grid.y; i++){
			
			tmpGrid = hist.getGrid(i, grid.y);
			if(tmpGrid != null){

				int evlu = 0; int est = 0;
				
				for(int j=0; j<tmpGrid.dotList.size(); j++){
					if(isChildOfOneElementInList(grid.dotList, tmpGrid.dotList.get(j))){
					//	tmpGrid.dotList.get(j).isChild = true;
						evlu++;
					}
				}
/*				for(int j=0; j<=Y_bar_index; j++){

					tmpGrid.barList_Y[j].active = true;
				}
*/					
				for(int j=0; j<tmpGrid.barList_X.length; j++){
					
					//if( !(tmpGrid.barList_X[j].least_Y > querybar_X.largest_Y)){
					if( tmpGrid.barList_X[j].least_Y <= grid.largest_y){
						
						//if(tmpGrid.barList_X[j].largest_Y > querybar_X.largest_Y){
/*						if(tmpGrid.barList_X[j].largest_Y > grid.largest_y){
							
							tmpGrid.barList_X[j].half_active = true;
							
							float abc = grid.largest_y - tmpGrid.barList_X[j].least_Y + 1;
							float abc2 = abc/(tmpGrid.barList_X[j].largest_Y - tmpGrid.barList_X[j].least_Y + 1);
							if(abc2 > tmpGrid.barList_X[j].covered_bar_area){
								tmpGrid.barList_X[j].covered_bar_area = abc2;
							}
							
						}
*/						//else{
							tmpGrid.barList_X[j].active = true;
							est+=tmpGrid.barList_X[j].cont.size();
						//}
						
					}
				
				}
				
				if(est<evlu){
					System.out.println("less than");
					System.out.println("est: "+est);
					System.out.println("evlu: "+evlu);
					System.exit(0);
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
				float a1=0; float a2=0; float a3=0;
				
				for(int k=0; k<curGrid.barList_X.length; k++){
					
					if(curGrid.barList_X[k].active){
						a1 = a1 + curGrid.barList_X[k].cont.size();
					}		
					else{
						/*if(curGrid.barList_X[k].half_active){
							a1 = (float) (a1 + 0.5*curGrid.barList_X[k].cont.size());
							System.out.println(0.5);
							System.out.println(curGrid.barList_X[k].covered_bar_area);
						}*/
						
						a1 = (float) (a1 + curGrid.barList_X[k].covered_bar_area*curGrid.barList_X[k].cont.size());
						//System.out.println(curGrid.barList_X[k].covered_bar_area);
					}
				}
//				for(int k=0; k<curGrid.barList_Y.length; k++){
					
//					if(curGrid.barList_Y[k].active){
//						a2 = a2 + curGrid.barList_Y[k].cont.size();
//					}					
//				}
				for(int k=0; k<curGrid.dotList.size(); k++){
					
					if(curGrid.dotList.get(k).isChild){
						a3++;
					}
				}
				
				//找出最大的来
				float a4 = Math.max(a1, a2);
				float a5 = Math.max(a3, a4);
				
				if(a5>0){
					System.out.println(curGrid.x+" "+curGrid.y);
					System.out.println("bar: "+a4);
					System.out.println("child: "+a3+"\n");
				}
				
				total = total + a5;
			}
		}
		
		//System.out.println("total is: "+total);
		
		return total;
	}
	
	public static void main(String[] args){
		
		int side_length = 300;
		double bar_width = 0.1;
		
		LastHist2 h = new LastHist2(side_length, bar_width);
		Hist2 h1 = new Hist2(side_length, bar_width);
		h1.loadHist("open_auction_queryNormal2.txt");
				
		Hist2 h2 = new Hist2(side_length, bar_width);
		h2.loadHist("incategory_targetNormal.txt");
		h.est(h1, h2);
		
		System.out.println("estimation: "+h.getResult());
		
		System.out.println("target grids need evaluation: "+h.num_of_target_grids_need_evaluation);
		
		System.out.println("bars need evlu: "+h.num_of_bars_need_evaluation_in_evaluated_target_grid);
		
		System.out.println("total bars: "+h.num_of_total_bars_in_evaluated_target_grid);
		
		
	}
}
