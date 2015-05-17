import java.util.Vector;
class Query implements Comparable<Query>{
	
	float realValue = 0;
	float estValue = 0;
	double estTime = 0;
	double realTime = 0;
	
	
    //public Vector<Integer> labels;
	public Vector<String> labels;
    public Query() {
	//labels = new Vector<Integer>();
	labels = new Vector<String>();
    }

    /*public void add(int i) {
	labels.add(Integer.valueOf(i));
    }*/
    
    public void add(String str){
    	labels.add(str);
    }
    
    public String toString() {
	String res = new String();
	for(int i = 0; i < labels.size(); i++) 
	    res = res + "//" + labels.elementAt(i);
	return ""+realValue+"/"+estValue+"|"+res;
    }

	@Override
	public int compareTo(Query arg0) {
		
		if(this.estValue > arg0.estValue){
			return 1;
		}
		else{
			if(this.estValue == arg0.estValue){
				return 0;
			}
			else{
				return -1;
			}		
		}
		
	}
}