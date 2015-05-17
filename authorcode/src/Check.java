import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Check {
	
	ArrayList<Position> query = new ArrayList();
	ArrayList<Position> target = new ArrayList();
	ArrayList<Position> childList = new ArrayList();
	int query_dot_result_dot_are_exactly_same = 0;
	int query_dot_result_dot_different = 0;
	int query_dot_result_dot_diff_left_end_same = 0;
	int query_dot_result_dot_diff_right_end_same = 0;
	
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
				query.add(p);
				line = in.readLine();
				count++;
			}
			
			in.close();
			System.out.println("query file has "+count+" lines");
			
		}
		catch(IOException e){
			System.out.println(e);
		}	
	}
	
	public void readTargetFile(String filename){
		
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
				target.add(p);
				line = in.readLine();
				count++;
			}
			
			in.close();
			
			System.out.println("target file has "+target.size()+" lines");
			
		}
		catch(IOException e){
			System.out.println(e);
		}		
	}
	
	public int countChild(){
		
		int count = 0;
		for(int i=0; i<target.size(); i++){
			
			if( isChildForList(query, target.get(i))){
				count++;
			}
		}
		System.out.println("child has: "+count);
		return count;
	}
	
	public int countChildAndWriteFile(){
		
		int count = 0;
		
		/*try{
			
			File outfile = new File("evaluationResult.txt");
			FileWriter fileW = new FileWriter(outfile);
			BufferedWriter out = new BufferedWriter(fileW);
						
			for(int i=0; i<target.size(); i++){
				
				if( isChildForList(query, target.get(i)) ){
					out.write(""+(int)target.get(i).x+" "+(int)target.get(i).y+"\n");
					count++;
				}
			}
			
			out.close();
		}
		catch(IOException e){
			
		}*/
		for(int i=0; i<target.size(); i++){
			
			if( isChildForList(query, target.get(i)) ){
				childList.add(target.get(i));
				count++;
			}
		}
		
		return count;
		
	}
	
	//为了把child count和文件读写分开以更合理的记录cpu时间
	public void writeChildrenToFile(){
		
		try{
		
			File outfile = new File("evaluationResult.txt");
			FileWriter fileW = new FileWriter(outfile);
			BufferedWriter out = new BufferedWriter(fileW);
					
			for(int i=0; i<childList.size(); i++){
			
				out.write(""+(int)childList.get(i).x+" "+(int)childList.get(i).y+"\n");
			}
		
			out.close();
		}
		catch(IOException e){
		
		}
	}
	
	public boolean isChildForList(ArrayList<Position> list, Position B){
		
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
			//System.out.println(A +" "+ B);
			//System.out.println(A);//找parent时候用
			//System.out.println(B);//找child时候用
			if(A.x == B.x && A.y == B.y){
				query_dot_result_dot_are_exactly_same++;
			}
			else{
				query_dot_result_dot_different++;
				if(A.x == B.x){
					query_dot_result_dot_diff_left_end_same++;
				}
				if(A.y == B.y){
					query_dot_result_dot_diff_right_end_same++;
				}
			}
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public boolean isParentForList(Position A, ArrayList<Position> list){
		
		for(int i=0; i<list.size(); i++){
			
			if( isChild(A, list.get(i))){
				return true;
			}
		}
		
		return false;		
	}
	
	
	public int countParent(){
		
		int count = 0;
		for(int i=0; i<query.size(); i++){
			
			if( isParentForList(query.get(i), target) ){
				count++;
			}
		}
		System.out.println(count);
		return count;
	}
	
	public void countParentAndWriteFile(String filename){
		
		int count = 0;
		try{
			File infile=new File(filename);
			FileWriter fileW = new FileWriter(infile);
			BufferedWriter o = new BufferedWriter(fileW);
			
			//File infile2=new File("QueryRedDotGenOpt.txt");
			//FileWriter fileW2 = new FileWriter(infile2,true);
			//BufferedWriter o2 = new BufferedWriter(fileW2);

			
			for(int i=0; i<query.size(); i++){
				if(isParentForList(query.get(i), target)){
					int tmp_x = (int)query.get(i).x;
					int tmp_y = (int)query.get(i).y;					
					o.write(tmp_x+" "+tmp_y+"\n");
					count++;
				}
			}
			//if(count>0){
			//o2.write(""+(query.size()/count)+"\n");
			//}
			o.close();
			//o2.close();
			
			//System.out.println("real parents have "+count);
		}
		catch(IOException e){
			
		}
	}
	
	
	public static void main(String[] args){
		
		Check c = new Check();
		long time1 = System.nanoTime();
		c.readQueryFile("open_auction_queryNormal2.txt");
		c.readTargetFile("incategory_targetNormal.txt");
		long time2 = System.nanoTime();
		System.out.println((time2-time1));
		System.out.println("abc");
		c.countChild();
		//c.countParent();
		//c.countParentAndWriteFile("watch_query2.txt");
		//System.out.println("query_dot_result_dot_are_exactly_same "+c.query_dot_result_dot_are_exactly_same);
		//System.out.println("query_dot_result_dot_different "+c.query_dot_result_dot_different);
		//System.out.println("query_dot_result_dot_diff_left_end_same "+c.query_dot_result_dot_diff_left_end_same);
		//System.out.println("query_dot_result_dot_diff_right_end_same "+c.query_dot_result_dot_diff_right_end_same);
		
	}
}
