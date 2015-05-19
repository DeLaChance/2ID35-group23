import java.util.ArrayList;


public class Bar {
	
	int value = 0;
	
	boolean self = false;
	boolean right = false;
	float self_largest_Y_cut = -1;
	float self_least_X_cut = Float.MAX_VALUE;
	float right_largest_Y_cut = -1;
	float self_X_area = 0;
	float self_Y_area = 0;
	float right_Y_area = 0;
	
	boolean active = false;
	float covered_bar_area = -1;
	
	float mean_Y = -1;
	
	
	//Bar X direction will use the following two
	float largest_Y = -1; 
	float least_Y = Float.MAX_VALUE;
	
	//Bar Y direction will use the following two
	float largest_X = -1;
	float least_X = Float.MAX_VALUE;
	
	ArrayList<Position> cont = new ArrayList();
	
	public void add(float x, float y){
		
		Position newPosition = new Position();
		newPosition.x = x;
		newPosition.y = y;
		
		this.cont.add(newPosition);
		
		if(x > largest_X){
			largest_X = x;
		}
		if(x < least_X){
			least_X = x;
		}
		if(y > largest_Y){
			largest_Y = y;
		}
		if(y < least_Y){
			least_Y = y;
		}
	}
	
	public void getMean(){
		
		float sum = 0;
		for(int i=0; i<this.cont.size(); i++){
			
			sum += this.cont.get(i).y;
		}
		
		this.mean_Y = sum/this.cont.size();
	}
	
	
}
