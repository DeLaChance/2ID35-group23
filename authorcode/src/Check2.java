import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Check2 {
	
	byte[] array1;
	
	public Check2(int length){
		array1 = new byte[length];
		
		//≥ı ºªØarray1
		for(int i=0; i<array1.length; i++){
			array1[i] = 0;
		}
	}
	
	public void readQueryFile(String filename){
		
		try{
			
			File infile=new File(filename);
			FileReader fileReader = new FileReader(infile);
			BufferedReader in = new BufferedReader(fileReader);
			
			String line = in.readLine();
			String[] tmp;
			int count = 0;
			while(line != null){
				
				tmp = line.split(" ");
				Position p = new Position();
				p.x = Integer.parseInt(tmp[0]);
				p.y = Integer.parseInt(tmp[1]);
				for(int i=(int) p.x; i<=p.y; i++){
					array1[i] = 1;
				}
				line = in.readLine();
				count++;
			}
			
			in.close();
			System.out.println("query file has "+count+"lines");
			
		}
		catch(IOException e){
			System.out.println(e);
		}	
	}
	
	public int readTargetFile(String filename){
		
		int childNum = 0;
		try{
			
			File infile=new File(filename);
			FileReader fileReader = new FileReader(infile);
			BufferedReader in = new BufferedReader(fileReader);
			
			int count = 0;
			
			String line = in.readLine();
			String[] tmp;
			while(line != null){
				
				tmp = line.split(" ");
				Position p = new Position();
				p.x = Integer.parseInt(tmp[0]);
				p.y = Integer.parseInt(tmp[1]);
				
				count++;
				if(isChild((int)p.x, (int)p.y)){
					childNum++;
				}
				
				line = in.readLine();
				count++;
			}
			
			in.close();
			
			System.out.println("target file has "+count+" lines");
			System.out.println("child has "+childNum);
			
			
		}
		catch(IOException e){
			System.out.println(e);
		}		
		
		return childNum;
	}
	
	public boolean isChild(int start, int end){
		
		for(int i=start; i<=end; i++){
			if(array1[i] == 0){
				return false;
			}
		}
		return true;
	}
	
	
	
	
	public static void main(String[] args){
		
		Check2 c = new Check2(106503);
		long time1 = System.nanoTime();
		c.readQueryFile("text_queryNormal.txt");
		c.readTargetFile("emph_targetNormal.txt");
		long time2 = System.nanoTime();
		System.out.println((time2-time1));
	}
}
