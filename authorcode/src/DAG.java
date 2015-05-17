import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;


/**
 * This class is used when doing a DFS over the data graph
 */
class StructInfo{
	
	public boolean isVisit;
	public int visitTime;
	
	public StructInfo(){
		
		this.isVisit=false;
		//this.visitTime=0;
	}
}


public class DAG extends DataGraph{
	
	public ArrayList<SCC2> SCCList; //gabow用的其实是下面的vertexSCCMap，但是为了更方便的使用每个SCC内的点，又把vertexSCCMap转存到这个里面	
	protected StructInfo[] vertexInfo;	
	protected HashMap<DataVertex, SCC> vertexSCCMap; //key is vertex		
	private Stack<Integer> stk1,stk2;	
	public static SCC singleVertexSCC = new SCC();	
	public static int timer = 1;
	public int SCC_count = 0;
	public int DagNodeCount = 0;
	
	int count = 0;
	
	public DAG(File file){
		
		super(file);
		
		SCCList = new ArrayList<SCC2>();
		
		vertexInfo=new StructInfo[this.getVerticesCount()];
		for(int i=0;i<this.getVerticesCount();i++){
			vertexInfo[i]=new StructInfo();
		}
		
		stk1 = new Stack<Integer>();
		stk1.ensureCapacity(this.idVertexMap.size());
		stk2 = new Stack<Integer>();
		stk2.ensureCapacity(this.idVertexMap.size());
		vertexSCCMap = new HashMap<DataVertex, SCC>();
		
	}
	
	
	public void graphReconstruction(){
						
		Iterator<DataVertex> it3,it4;
		
		for(int i=0; i<SCCList.size(); i++){
			
			SCC2 curSCC = SCCList.get(i);
			//对当前的SCC，创建一个super vertex
			DataVertex newSuperVertex = new DataVertex(curSCC);
			newSuperVertex.SCCCorrespond = curSCC.id;
			//把新创建的super vertex加入到点集列表中去
			try {
				this.add(newSuperVertex);
				this.idVertexMap.put(newSuperVertex.id, newSuperVertex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for(int j=0; j<curSCC.member.size(); j++){
				
				DataVertex curVertex = curSCC.member.get(j);
				
				it3=this.getIncomingAdjacentVertices(curVertex).iterator();
				it4=this.getOutgoingAdjacentVertices(curVertex).iterator();
				
				DataVertex tmp;
				try {
					while(it3.hasNext()){
						tmp=it3.next();
						if( !curSCC.containsVertex(tmp) && this.getEdge(tmp, newSuperVertex)==null){
							this.addEdge(tmp, newSuperVertex);}
						this.removeEdge(this.getEdge(tmp, curVertex));
					}
					while(it4.hasNext()){
						tmp=it4.next();
						if( !curSCC.containsVertex(tmp) && this.getEdge(newSuperVertex, tmp)==null){
							this.addEdge(newSuperVertex, tmp);}
						this.removeEdge(this.getEdge(curVertex, tmp));
					}
					this.remove(curVertex);
					this.idVertexMap.remove(curVertex.id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("The DAG has node num "+this.getVerticesCount());
		System.out.println("The DAG has node num "+this.idVertexMap.size());
		
		DagNodeCount = this.idVertexMap.size();
	}
	
	
	
	/**
	 *  这个函数把vertex放进了对应的SCC中
	 */
	public void gabowSCCAlgorithm(){
		
		DataVertex dv = this.root;
		DFS(dv,1);
		
		vertexSCCMap.clear(); //为了节约内存
		stk1.clear(); //为了节约内存
		stk2.clear(); //为了节约内存
	}
	
	public void DFS(DataVertex dv, int level){

		/*count++;
		if(count > 700000){
			System.exit(0);
		}*/
		//dv.level=level;
		vertexInfo[dv.id].isVisit=true;
		vertexInfo[dv.id].visitTime=timer;
		timer++;
		stk1.push(dv.id);stk2.push(dv.id);
		level++;
		Iterator<DataVertex> it=this.getOutgoingAdjacentVertices(dv).iterator();
		DataVertex du;
		while(it.hasNext()){
			du=it.next();
			if(vertexInfo[du.id].isVisit==false)
				DFS(du,level);
			else{
				if(vertexSCCMap.get(du)==null){
					while(vertexInfo[stk2.peek().intValue()].visitTime>vertexInfo[du.id].visitTime){
						stk2.pop();
					}
				}
			}
		}
		
		if(stk2.peek().intValue()==dv.id){
			if(stk1.peek().intValue()==dv.id){
				vertexSCCMap.put(this.idVertexMap.get(dv.id), singleVertexSCC);
				stk1.pop();stk2.pop();
			}
			else{
				// (v is the root of a SCC, generate a SCC from stk1)
				System.out.print(stk1.peek()+":"+stk2.peek()+"-----One SCC Finished\n");
				stk2.pop();
				SCC scc_v=new SCC();
				SCC2 newSCC = new SCC2();
				newSCC.id = SCC_count;
				SCC_count++;

				int w;
				do{
					w=stk1.pop().intValue();
					System.out.print(w+" ");
					scc_v.vertexIDSet.add(w);
					vertexSCCMap.put(this.idVertexMap.get(w),scc_v);
					newSCC.member.add(this.idVertexMap.get(w));
				}while(w!=dv.id);
				SCCList.add(newSCC);
				System.out.print(":::::"+scc_v.vertexIDSet.size()+"\n");
			}
		}
	}
	

	public static void main(String[] args){
		
		File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");
		//File xmlFile = new File("../data/xmark3/xmark0.xml");
		//File xmlFile=new File("auction.xml");
	    DAG dag = new DAG(xmlFile); 
	    dag.gabowSCCAlgorithm();
	    dag.graphReconstruction();
	    //System.out.println(dag.idVertexMap);
	    System.out.println(DataVertex.labelidmap.size());
	    System.out.println("Dag edge has: "+dag.getEdgesCount());
	}
}
