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
	
	//�������histogram���ж��ٸ��ǿյ�grid
	public int get_grid_num(){
		
		return this.intermediate.get_grid_num();
	}
	
	public void upDateLeastXandLargestY(){
		
		//����һ��inermediate��Ŀ���Ǹ��¸���grid����Сx�����y������grid��¼��x����y��
		ArrayList<HashMap<Integer, Grid>> x_list2 = new ArrayList(this.intermediate.cont.values());
		
		for(int i=0; i<x_list2.size(); i++){
			
			ArrayList<Grid> y_list2 = new ArrayList(x_list2.get(i).values());
			for(int j=0; j<y_list2.size(); j++){
				
				Grid curGrid = y_list2.get(j);
				
				//���curGrid.knif_x��curGrid.knif_y��Ϊ��ʼֵ��
				//��ôcurGridһ���ǶԽ��߷���
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
		//����grid��downλ�õ�tmpGrid�У�tmpGrid.x=grid.x, grid.x<=tmpGrid.y<grid.y
		//�ң���tmpGrid.y==grid.xʱ��tmpGrid���ڶԽ���λ��
				
		//�ȴ���ǶԽ����ϵ�grid
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
				
		//�ٴ���Խ����ϵ�grid, ��evaluation
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
		//����grid��rightλ���ϵ�grid�У�tmpGrid.y=grid.y; grid.x<tmpGrid.x<=grid.y
		//�ң���tmpGrid.x==grid.yʱ��tmpGrid���ڶԽ���λ��
		
		//�ȴ���ǶԽ����ϵ�grid
		for(int i=grid.x+1; i<grid.y; i++){
			
			tmpGrid = hist.getGrid(i, grid.y);
			if(tmpGrid != null){
				
				tmpGrid.knif_y = Math.max(tmpGrid.knif_y, grid.largest_y);
				this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
			}			
		}
		
		//�ٴ���Խ����ϵ�grid�� ��evaluation
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
		//����descλ�õ�grid��:(x ����) grid.x+1<=tmpGrid.x<grid.y
		for(int i=grid.x+1; i<grid.y; i++){
			
			//ҲҪ���ֶԽ���λ��
			//�ȴ���ǶԽ���λ��
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
			
			//�ٴ���Խ���λ��,dotList��Ԫ�ص�isChildȫ��Ϊtrue
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
				
				//���grid�ǶԽ���λ�ã�Ҫevaluation
				for(int i=0; i<tmpGrid.dotList.size(); i++){
					if(isChildOfOneElementInList(grid.dotList, tmpGrid.dotList.get(i))){
						tmpGrid.dotList.get(i).isChild = true;
					}
				}
				
			}
			else{
				
				//���grid�ǶԽ���λ�ã�Ҫ����knif_x��knif_y
				tmpGrid.knif_x = Math.min(tmpGrid.knif_x, grid.least_x);
				tmpGrid.knif_y = Math.max(tmpGrid.knif_y, grid.largest_y);
			}
			
			this.intermediate.addGrid(tmpGrid.x, tmpGrid.y, tmpGrid);
		}		
	}
	
	
	//�ж�B�ǲ���list��ĳһ����child
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
					//�Խ����ϵķ��񣬲�������Сx�����y����ʾ
					if(curGrid.x == curGrid.y){
						for(int k=0; k<curGrid.dotList.size(); k++){
							//ֻ���isChild=true��
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
