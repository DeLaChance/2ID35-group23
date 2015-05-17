import java.util.ArrayList;

/*
 * 这个是没有压缩功能的table
 */
public class Table0 {

	ArrayList< ArrayList<Integer> > cont = new ArrayList();
	
	public Table0(int length){
		
		for(int i=0; i<length; i++){
			
			ArrayList<Integer> list = new ArrayList();
			cont.add(list);
		}
	}
	
	public void addDup(int primitive, int new_dup){
		
		cont.get(primitive).add(new_dup);
	}
	
	public ArrayList<Integer> getDupSet(int primitive){
		
		return this.cont.get(primitive);
	}
	
	public static void main(String[] args){
		
		Table0 t = new Table0(3);
		
		t.addDup(1, 10);
		t.addDup(2, 22);
		
		System.out.println(t.cont.get(2));
	}
}
