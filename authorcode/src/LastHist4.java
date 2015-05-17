import java.util.ArrayList;
import java.util.HashMap;


public class LastHist4 {

	Hist2 intermediate;
	float side_length;
	double bar_width;
	
	Hist2 query;
	Hist2 target;
	
	int selfGrid = 0;
	int evlSelfGrid = 0;

	float abcd = 0;
	int real = 0;

	
	public LastHist4(float size, double bar_width_input){
		
		side_length = size;
		bar_width = bar_width_input;
	}
	
	public void est(Hist2 histA, Hist2 histB){
	
		query = histA; target = histB;
		
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
			//	getDown(curGrid, histB);
				//getRight
			//	getRight(curGrid, histB);
				//getDesc
			//	getDesc(curGrid, histB);
			}
		}
	}
	
	public void getSelf(Grid grid, Hist2 hist){
		
			
		Grid targetGrid = hist.getGrid(grid.x, grid.y);
		if(targetGrid != null){
			
			selfGrid++;
			
			//如果tmpGrid和grid都是diagonal grid，特殊处理evaluation
			//否则，用bar统计数据
			/*if(targetGrid.x == targetGrid.y)
			{
				
				for(int i=0; i<targetGrid.dotList.size(); i++){
					if(isChildOfOneElementInList(grid.dotList, targetGrid.dotList.get(i))){
						targetGrid.dotList.get(i).isChild = true;
					}
				}
				this.intermediate.addGrid(targetGrid.x, targetGrid.y, targetGrid);
				
				evlSelfGrid++;
			}
			else*/
			{
							
				//发现如果右边或者下边全算的话，会引入大量的误差
				//用bar的least 和 largest 去refine估计
				//当target bar的least > query bar的largest时，
				//不用active target bar了
			
				for(int i = 0; i<grid.barList_X.length; i++){
					
					Bar queryBar = grid.barList_X[i];
					
					if(queryBar.cont.size() > 0){
						
						//Self
						Bar targetSelfBar = targetGrid.barList_X[i];
						
						if(targetSelfBar.cont.size() > 0){
							
							targetSelfBar.self = true;
							
							for(int h=0; h<queryBar.cont.size(); h++){
								
							}
							
							//set self_largest_Y_cut
							if(queryBar.largest_Y > targetSelfBar.self_largest_Y_cut){
								
								targetSelfBar.self_largest_Y_cut = queryBar.largest_Y;
							}
							
							//set self_least_X_cut
							if(queryBar.least_X < targetSelfBar.self_least_X_cut){
								
								targetSelfBar.self_least_X_cut = queryBar.least_X;
							}
						}
						
						//Right
						/*for(int j=i+1; j<targetGrid.barList_X.length; j++){
							
							Bar targetRightBar = targetGrid.barList_X[j];
							
							if(targetRightBar.cont.size() > 0){
								
								targetRightBar.right = true;
								
								//set right_largest_Y_cut
								if(queryBar.largest_Y > targetRightBar.right_largest_Y_cut){
									
									targetRightBar.right_largest_Y_cut = queryBar.largest_Y;
								}
							}
						}*/
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
					
					
					
					Bar curBar = curGrid.barList_X[k];
					
					if(curBar.cont.size() > 0){
						
						for(int h=0; h<curBar.cont.size(); h++){
							Position curDot = curBar.cont.get(h);
							
							if(curDot.x >= curBar.self_least_X_cut 
									&& curDot.y <= curBar.self_largest_Y_cut){
								real++;
							}
						}
						
						float self = 0; float right = 0; //由self和right所确定的点数
						
						//看一看self
						if(curBar.self){
							
							
							
							if(curBar.self_largest_Y_cut >= curBar.largest_Y){
								
							
								//就只看x就可以
								curBar.self_Y_area = 1;
								
								if(curBar.self_least_X_cut <= curBar.least_X){
									
									//那就全部都算
									curBar.self_X_area = 1;
								
								}
								else if(curBar.self_least_X_cut <= curBar.largest_X){
									
									float diff = curBar.largest_X - curBar.self_least_X_cut + 1;
									float range = curBar.largest_X - curBar.least_X + 1;
									curBar.self_X_area = diff/range;
									
								}
						
								
								int sum = 0;
								for(int h=0; h<curBar.cont.size(); h++){
									if(curBar.cont.get(h).x >= curBar.self_least_X_cut
											&& curBar.cont.get(h).y <= curBar.self_largest_Y_cut){
										sum++;
									}
								}
								float est = curBar.cont.size()*curBar.self_X_area;								
								float est2 = est*curBar.self_Y_area;
							
							}
							else if(curBar.self_largest_Y_cut >= curBar.least_Y){
								
								float diff = curBar.self_largest_Y_cut - curBar.least_Y + 1;
								float range = curBar.largest_Y - curBar.least_Y + 1;
								curBar.self_Y_area = diff/range;
								
								//再看x area
								if(curBar.self_least_X_cut <= curBar.least_X){
									
									//那就全部算
									curBar.self_X_area = 1;
								}
								else if(curBar.self_least_X_cut <= curBar.largest_X){
									
									float diff2 = curBar.largest_X - curBar.self_least_X_cut + 1;
									float range2 = curBar.largest_X - curBar.least_X + 1;
									curBar.self_X_area = diff2/range2;
									
								}
								
								int sum = 0;
								for(int h=0; h<curBar.cont.size(); h++){
									if(curBar.cont.get(h).x >= curBar.self_least_X_cut
											&& curBar.cont.get(h).y <= curBar.self_largest_Y_cut){
										sum++;
									}
								}
								float est = curBar.cont.size()*curBar.self_X_area;								
								float est2 = est*curBar.self_Y_area;

							}
						}
						
						/*//再看right
						if(curBar.right){
							
							if(curBar.right_largest_Y_cut >= curBar.largest_Y){
								
								curBar.right_Y_area = 1;						
								
								//System.out.print(curBar.right_Y_area+" ");
							}
							else if(curBar.right_largest_Y_cut >= curBar.least_Y){
								
								float diff = curBar.right_largest_Y_cut - curBar.least_Y + 1;
								float range = curBar.largest_Y - curBar.least_Y + 1;
								curBar.right_Y_area = diff/range;
								
								//System.out.print(curBar.right_Y_area+" ");
							}
						}*/
						
						//现在就来算算面积
						float est = curBar.cont.size()*curBar.self_X_area;
						
						float est2 = est*curBar.self_Y_area;
						
						a1 += (int)est2;
						
					}
					
				}
				
				for(int k=0; k<curGrid.dotList.size(); k++){
					
					if(curGrid.dotList.get(k).isChild){
						a3++;
					}
				}
								
				//float a5 = a3+a1;
				//total = total + a5;
				
				total = total + a1;
			}
		}
		
		//System.out.println("total is: "+total);
		
		return total;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args){
		
		int side_length = 500;
		double bar_width = 0.1;
		
		LastHist4 h = new LastHist4(side_length, bar_width);
		Hist2 h1 = new Hist2(side_length, bar_width);
		h1.loadHist("open_auction_queryNormal2.txt");
				
		Hist2 h2 = new Hist2(side_length, bar_width);
		h2.loadHist("incategory_targetNormal.txt");
		h.est(h1, h2);
		
		System.out.println("estimation: "+h.getResult());
		
		System.out.println("self grid: "+h.selfGrid);
		System.out.println("evl self grid: "+h.evlSelfGrid);

		System.out.println(h.abcd);
		System.out.println(h.real);
	}
	
}
