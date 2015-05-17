import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class Test {

	
	
	public static void main(String[] args) {
		
		byte[] abc = new byte[10];
		abc[0] = 1; 
		abc[1] = 0;
		abc[2] = 1;
		abc[3] = 0;
		abc[4] = 1;
		abc[5] = 0;
		abc[6] = 0;
		abc[7] = 0;
		abc[8] = 0;
		abc[9] = 1;
		

		int start_address = 0;
		int end_address = 0;
	
		start_address = findOne(abc, 0);
		System.out.print(start_address+" ");
		
		while(true){
			
			end_address = findZero(abc, start_address);
			if(end_address == -1){
				System.out.println(abc.length-1);
				break;
			}
			System.out.println(end_address-1);
			start_address = findOne(abc, end_address);
			if(start_address == -1){
				break;
			}
			System.out.print(start_address+" ");
		}
		
	}

	public static int findOne(byte[] array, int cur_pos){
		
		for(int i=cur_pos; i<array.length; i++){
			if(array[i] == 1){
				return i;
			}
		}
		return -1;
	}
	
	public static int findZero(byte[] array, int cur_pos){
		
		for(int i=cur_pos; i<array.length; i++){
			if(array[i] == 0){
				return i;
			}
		}
		return -1;
	}
}
