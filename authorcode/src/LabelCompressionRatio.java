import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 点的label先求差，然后再压缩
 * 从CNeighborZip文件中读取
 * @author ypeng
 *
 */
public class LabelCompressionRatio {

	public void readLabelFile(int scaningfactor){
		
		int line_num = 0;
		
		String address = "D:/MyProject/Merging10/Out_Files/";
		try{
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File(address+"CNeighborZipFS_"+scaningfactor+".txt")));
			
			BufferedReader in2 = new BufferedReader(
					new FileReader(
					new File(address+"CNeighborZipT_"+scaningfactor+".txt")));
			
			DataOutputStream out=new DataOutputStream(   
                    new BufferedOutputStream(   
                    new FileOutputStream(""+scaningfactor+"_B")));  
			
			
			//==============
			//读入CNeighborZipFS
			String line = in.readLine();
			String[] tmp;
			
			while(line != null){
				
				System.out.println(line_num);
				
				tmp = line.split(" ");
				
				int[] original = new int[tmp.length];
				
				for(int i=0; i<tmp.length; i++){
					original[i] = Integer.parseInt(tmp[i]);
				}
				
				int[] diff = new int[tmp.length];
				
				diff[0] = original[0];
				
				for(int i=1; i<tmp.length; i++){
					
					diff[i] = original[i] - original[i-1];
				}
				
				for(int i=0; i<tmp.length; i++){
					
					out.writeInt(diff[i]);
					//System.out.print(diff[i]+" ");
				}
				//System.out.println();
				
				line = in.readLine();
				line_num++;
			}
			//CNeighborZipFS==========结束
			
			//==================
			//读入CNeighborZipT			
			String line2 = in2.readLine();
			line2 = in2.readLine();
			line_num++;
			
			String[] tmp2;
			
			while(line2 != null){
				
				System.out.println(line_num);
				
				tmp2 = line2.split(" ");
				
				int[] original = new int[tmp2.length];
				
				for(int i=0; i<tmp2.length; i++){
					original[i] = Integer.parseInt(tmp2[i]);
				}
				
				int[] diff = new int[tmp2.length];
				
				diff[0] = original[0];
				
				for(int i=1; i<tmp2.length; i++){
					
					diff[i] = original[i] - original[i-1];
				}
				
				for(int i=0; i<tmp2.length; i++){
					
					out.writeInt(diff[i]);
					//System.out.print(diff[i]+" ");
				}
				//System.out.println();
				
				line2 = in2.readLine();
				line_num++;
			}
			
			in.close();
			out.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public static void main(String[] args){
		
		LabelCompressionRatio lcr = new LabelCompressionRatio();
		
		lcr.readLabelFile(30);
	}
	
}
