   import java.util.*;

// Query generator
  class Gen2 {
    //
    // quick and dirty global variables.
    //
    static public int max_length;
    static public int min_length;
    static public int max_label;
    static public int total_elements;
    static private Random rand;

    //BC: need to initialize from other places
    static private HashMap<String,Integer> labelidmap;
    static private HashMap<String, ArrayList<Integer> > labelToVertexIDMap;
    static private HashMap<Integer, String> idlabelmap;//labelidmap的反

    //
    // constructor
    //
    public Gen2(int min_length, int max_length, HashMap<String,
    		ArrayList<Integer> > labelToVertexIDMapInput,
    		HashMap<String, Integer> labelidmapInput) {
	this.min_length = min_length;
	this.max_length = max_length;
        rand = new Random(System.currentTimeMillis());
	this.total_elements = 0;
	
	labelidmap = labelidmapInput;
	labelToVertexIDMap = labelToVertexIDMapInput;
	idlabelmap = new HashMap();

	//把labelidmap翻转
	ArrayList<String> key_list = new ArrayList(labelidmap.keySet());
	for(int i=0; i<key_list.size(); i++){
		int v = labelidmap.get(key_list.get(i));
		idlabelmap.put(v, key_list.get(i));
	}

	/*// START: for testing only
	labelidmap = new HashMap<String,Integer>();
	labelToVertexIDMap = new HashMap<String, ArrayList<Integer> >();
	labelidmap.put("xmark", 0);
	labelidmap.put("open_auction", 1);
	labelidmap.put("person", 2);
	labelidmap.put("bidder", 3);
	labelidmap.put("persons", 4);


	ArrayList<Integer> a0 = new ArrayList<Integer>();
	a0.add(1); a0.add(2); a0.add(3); a0.add(4); a0.add(5);
	labelToVertexIDMap.put("xmark", a0);
	ArrayList<Integer> a1 = new ArrayList<Integer>();
	a1.add(1); a1.add(2); a1.add(3); a1.add(4);
	labelToVertexIDMap.put("open_auction", a1);
	ArrayList<Integer> a2 = new ArrayList<Integer>();
	a2.add(1); a2.add(2); a2.add(3); 
	labelToVertexIDMap.put("person", a2);
	ArrayList<Integer> a3 = new ArrayList<Integer>();
	a3.add(1); a3.add(2); 
	labelToVertexIDMap.put("bidder", a3);
	ArrayList<Integer> a4 = new ArrayList<Integer>();
	a4.add(1);
	labelToVertexIDMap.put("persons", a4);

	// END: for testing only
*/

	Iterator<Map.Entry<String, ArrayList<Integer> > > itr  = labelToVertexIDMap.entrySet().iterator();
	while(itr.hasNext())
	    total_elements = total_elements + ((ArrayList<Integer>)((itr.next()).getValue())).size();
		
	max_label = labelidmap.size();
    }


    private static int getRandom(int mod) {
	return Math.abs(rand.nextInt()) % mod;
    }

    //biased labels
    private static int getRandomLabel() {
	int r =  Math.abs(rand.nextInt()) % total_elements;

	Iterator<Map.Entry<String, ArrayList<Integer> > > itr  = labelToVertexIDMap.entrySet().iterator();
	while(itr.hasNext()) {
	    Map.Entry<String, ArrayList<Integer> > e = itr.next();
	    r = r - ((ArrayList<Integer>)(e.getValue())).size();
	    if(r < 0) 
		return (labelidmap.get(e.getKey())).intValue();	    
	}

	
	System.out.println("Error: should not reach here");
	System.exit(1);
	return 1;
    }


    // this is the only function called by outside
    public Query getQuery() {
    	Query q = new Query();
    	int offset = getRandom(max_length - min_length +1);

    	for(int i=0; i < min_length + offset; i++) {
    		//int next_id = getRandom(max_label);   //evenly distributed labels
    		int next_id = getRandomLabel();         //baised labels
    		//q.add(next_id);
    		//q.add(labelidmap.get(next_id));
    		q.add(idlabelmap.get(next_id));
    	}
    	return q;
    }



    //
    // testing code
    //
    public static void main(String[] args) {    

	
    }
}