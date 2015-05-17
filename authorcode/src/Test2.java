
public class Test2 {

	public static void main(String[] args){
		
		int[] scanfactor = {80, 70, 60, 50};
		int[] startOfThird = {1201348, 1052521, 898589, 747755};
		
		for(int z=0; z<4; z++){
		
			int scaningfactor = scanfactor[z];
			int start_of_third = startOfThird[z];
		
			System.out.println(scaningfactor + " " + start_of_third);
		}
	}
}
