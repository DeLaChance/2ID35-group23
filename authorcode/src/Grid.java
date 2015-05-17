import java.util.ArrayList;
import java.util.HashSet;

/*
 * query估计时，query的中间step用最小x，最大y
 * 最后一个step用均匀分布假设，按照面积来估计
 * 但是对对角线上的grid不能用最小x，最大y，比如(3,3)(8,8)用3和8就不对了
 */
public class Grid {
	
	double bar_width;
	
	int side_length;
	int x = 0;
	int y = 0;
	int value = 0;
	
	float self = 0;
	float down = 0;
	float right = 0;
	float desc = 0;
	
	float least_x = Float.MAX_VALUE; //最小x，最大y在query的中间的step用
	float largest_y = -1;
	
	float knif_x = 100000000; //记录对当前grid来讲最大的x刀
	float knif_y = -1; //记录对当前grid来讲最大的y刀
	
	float covered_area = -1; //用于self估计时，按照面积来（只在query的最后一个step用）
	
	Bar[] barList_X;
	Bar[] barList_Y;
	

	ArrayList<Position> dotList = new ArrayList();
	
	//boolean isD = false;
	
	
	public Grid(int side_length_input, double bar_width_input){
		
		side_length = side_length_input;
		bar_width = bar_width_input;
		
		barList_X = new Bar[(int)(1/bar_width)]; //X方向上的bar
		barList_Y = new Bar[(int)(1/bar_width)]; //Y方向上的bar
		
		for(int i=0; i<1/bar_width; i++){
			barList_X[i] = new Bar();
			barList_Y[i] = new Bar();
		}
	}
	
	//加进来的是点的真正的坐标
	public void add(float realX, float realY){
		
		//更新最小x和最大y
		if(realX < this.least_x){
			this.least_x = realX;
		}
		if(realY > this.largest_y){
			this.largest_y = realY;
		}
		
		this.value ++;
		
		//把当前dot，加入当前grid的dot列表中
		Position newPosition = new Position();
		newPosition.x = realX;
		newPosition.y = realY;
		this.dotList.add(newPosition);
		
		//统计bar数据
		int diff_X = (int)(realX - (this.x * side_length));
		int index_X = (int)(diff_X/(side_length * bar_width));
		this.barList_X[index_X].value++;
		
		//不单单是统计，还要把dot加入到bar的dot list中去
		this.barList_X[index_X].add(realX,realY);
		
		
		int diff_Y = (int)(realY - (this.y * side_length));
		int index_Y = (int)(diff_Y/(side_length * bar_width));
		this.barList_Y[index_Y].value++;
		
		//不单单是统计，还要把dot加入到bar的dot list中去
		this.barList_Y[index_Y].add(realX, realY);

	}
	
	//返回该grid中，不为空的X bar的数目
	public int getBarNum_X(){
		
		int count = 0;
		for(int i=0; i<(1/bar_width); i++){
			if(this.barList_X[i].value > 0){
				count++;
			}
		}
		return count;
	}
	
	//返回该grid中，从pos往右走的不为空的bar的数目（含pos）
	public int getBarNum_X(int pos){
		
		int count = 0;
		for(int i=pos; i<(1/bar_width); i++){
			
			if(this.barList_X[i].value > 0){
				count++;
			}
		}
		return count;
	}
	
	//返回该grid中，最左边的不为空的bar的位置
	public int getLeftMostBar(){
		
		for(int i=0; i<(1/bar_width); i++){
			if(this.barList_X[i].value > 0){
				return i;
			}
		}
		return (int)(1/bar_width);
	}
	
	//返回该grid中，不为空的X bar的数目
	public int getBarNum_Y(){
		
		int count = 0;
		for(int i=0; i<(1/bar_width); i++){
			if(this.barList_Y[i].value > 0){
				count++;
			}
		}
		return count;
	}
	
	//返回该grid中，从pos往下走的不为空的bar的数目（含pos）
	public int getBarNum_Y(int pos){
		
		int count = 0;
		for(int i=0; i<=pos; i++){
			if(this.barList_Y[i].value > 0){
				count++;
			}
		}
		return count;
	}
	
	//返回该grid中，最上边的不为空的bar的位置
	public int getUpMostBar(){
		
		for(int i=(int)((1/bar_width)-1); i>=0; i--){
			if(this.barList_Y[i].value > 0){
				return i;
			}
		}
		return -1;
	}
	
	//输入参数是一个bar和pos，看看pos往右（含pos）的不空的bar中
	//有多少个是需要和bar进行evaluation的
	public int getBarNum_X(Bar bar, int pos){
		
		int count = 0;
		for(int i=pos; i<(1/bar_width); i++){
			
			//先判断是不空的
			if(this.barList_X[i].value > 0){
				
				//再判断是要与bar进行evaluation的
				if( !(this.barList_X[i].least_Y > bar.largest_Y
						|| this.barList_X[i].largest_Y <= bar.largest_Y) ){
					count++;
				}		
			}
		}
		return count;
	}

	//返回位于pos的x方向上的bar
	public Bar getBar_X(int pos){
		return this.barList_X[pos];
	}
	
	public String toString(){
		
		/*if(x==y){
			return "D"+x+","+y+"->"+dotList.toString();
		}
		else{
			return ""+x+","+y+"->"+value;
		}*/
		
		return ""+x+","+y+"->"+dotList;
	}
}
