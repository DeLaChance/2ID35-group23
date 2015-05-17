import java.util.ArrayList;
import java.util.Vector;


public class SCC2{
	
	public int id = -1;
	public static int counter = 0;
	public ArrayList<DataVertex> member;
	
	public SCC2(){
		
		member = new ArrayList<DataVertex>();	
		id = counter;
		counter++;
	}
	
	public boolean containsVertex(DataVertex v){
		
		for(int i=0; i<member.size(); i++){
			if(member.get(i).id == v.id){
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		
		return member.toString();
	}
}
