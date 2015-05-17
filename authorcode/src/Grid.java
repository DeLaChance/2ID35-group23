import java.util.ArrayList;
import java.util.HashSet;

/*
 * query����ʱ��query���м�step����Сx�����y
 * ���һ��step�þ��ȷֲ����裬�������������
 * ���ǶԶԽ����ϵ�grid��������Сx�����y������(3,3)(8,8)��3��8�Ͳ�����
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
	
	float least_x = Float.MAX_VALUE; //��Сx�����y��query���м��step��
	float largest_y = -1;
	
	float knif_x = 100000000; //��¼�Ե�ǰgrid��������x��
	float knif_y = -1; //��¼�Ե�ǰgrid��������y��
	
	float covered_area = -1; //����self����ʱ�������������ֻ��query�����һ��step�ã�
	
	Bar[] barList_X;
	Bar[] barList_Y;
	

	ArrayList<Position> dotList = new ArrayList();
	
	//boolean isD = false;
	
	
	public Grid(int side_length_input, double bar_width_input){
		
		side_length = side_length_input;
		bar_width = bar_width_input;
		
		barList_X = new Bar[(int)(1/bar_width)]; //X�����ϵ�bar
		barList_Y = new Bar[(int)(1/bar_width)]; //Y�����ϵ�bar
		
		for(int i=0; i<1/bar_width; i++){
			barList_X[i] = new Bar();
			barList_Y[i] = new Bar();
		}
	}
	
	//�ӽ������ǵ������������
	public void add(float realX, float realY){
		
		//������Сx�����y
		if(realX < this.least_x){
			this.least_x = realX;
		}
		if(realY > this.largest_y){
			this.largest_y = realY;
		}
		
		this.value ++;
		
		//�ѵ�ǰdot�����뵱ǰgrid��dot�б���
		Position newPosition = new Position();
		newPosition.x = realX;
		newPosition.y = realY;
		this.dotList.add(newPosition);
		
		//ͳ��bar����
		int diff_X = (int)(realX - (this.x * side_length));
		int index_X = (int)(diff_X/(side_length * bar_width));
		this.barList_X[index_X].value++;
		
		//��������ͳ�ƣ���Ҫ��dot���뵽bar��dot list��ȥ
		this.barList_X[index_X].add(realX,realY);
		
		
		int diff_Y = (int)(realY - (this.y * side_length));
		int index_Y = (int)(diff_Y/(side_length * bar_width));
		this.barList_Y[index_Y].value++;
		
		//��������ͳ�ƣ���Ҫ��dot���뵽bar��dot list��ȥ
		this.barList_Y[index_Y].add(realX, realY);

	}
	
	//���ظ�grid�У���Ϊ�յ�X bar����Ŀ
	public int getBarNum_X(){
		
		int count = 0;
		for(int i=0; i<(1/bar_width); i++){
			if(this.barList_X[i].value > 0){
				count++;
			}
		}
		return count;
	}
	
	//���ظ�grid�У���pos�����ߵĲ�Ϊ�յ�bar����Ŀ����pos��
	public int getBarNum_X(int pos){
		
		int count = 0;
		for(int i=pos; i<(1/bar_width); i++){
			
			if(this.barList_X[i].value > 0){
				count++;
			}
		}
		return count;
	}
	
	//���ظ�grid�У�����ߵĲ�Ϊ�յ�bar��λ��
	public int getLeftMostBar(){
		
		for(int i=0; i<(1/bar_width); i++){
			if(this.barList_X[i].value > 0){
				return i;
			}
		}
		return (int)(1/bar_width);
	}
	
	//���ظ�grid�У���Ϊ�յ�X bar����Ŀ
	public int getBarNum_Y(){
		
		int count = 0;
		for(int i=0; i<(1/bar_width); i++){
			if(this.barList_Y[i].value > 0){
				count++;
			}
		}
		return count;
	}
	
	//���ظ�grid�У���pos�����ߵĲ�Ϊ�յ�bar����Ŀ����pos��
	public int getBarNum_Y(int pos){
		
		int count = 0;
		for(int i=0; i<=pos; i++){
			if(this.barList_Y[i].value > 0){
				count++;
			}
		}
		return count;
	}
	
	//���ظ�grid�У����ϱߵĲ�Ϊ�յ�bar��λ��
	public int getUpMostBar(){
		
		for(int i=(int)((1/bar_width)-1); i>=0; i--){
			if(this.barList_Y[i].value > 0){
				return i;
			}
		}
		return -1;
	}
	
	//���������һ��bar��pos������pos���ң���pos���Ĳ��յ�bar��
	//�ж��ٸ�����Ҫ��bar����evaluation��
	public int getBarNum_X(Bar bar, int pos){
		
		int count = 0;
		for(int i=pos; i<(1/bar_width); i++){
			
			//���ж��ǲ��յ�
			if(this.barList_X[i].value > 0){
				
				//���ж���Ҫ��bar����evaluation��
				if( !(this.barList_X[i].least_Y > bar.largest_Y
						|| this.barList_X[i].largest_Y <= bar.largest_Y) ){
					count++;
				}		
			}
		}
		return count;
	}

	//����λ��pos��x�����ϵ�bar
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
