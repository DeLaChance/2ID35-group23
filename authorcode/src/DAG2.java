import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


public class DAG2 extends DataGraph{
	
	int index = 0;
	Stack<Tmp> stk = new Stack();
	Stack<DataVertex> stk2 = new Stack();
	ArrayList<SCC2> SCCList = new ArrayList();
	HashMap<Integer, List<DataVertex> > adjList = new HashMap();
	
	public DAG2(File xmlFile){
		
		super(xmlFile);
		Iterator<DataVertex> it = this.idVertexMap.values().iterator();
		while(it.hasNext()){
			
			DataVertex curVertex = it.next();
			List<DataVertex> outgoing = this.getOutgoingAdjacentVertices(curVertex);
			adjList.put(curVertex.id, outgoing);
		}
		stk.ensureCapacity(this.getVerticesCount());
	}
	
	
	public void dfs2(){
		
		//用两个栈，这两个栈出栈的条件不同
		//stk当栈顶元素的孩子已经全部遍历过，弹出栈顶元素
		//stk2当栈顶元素low=dfn时，弹出栈顶元素
		
		index = 0; //临时变量，比如，点n的邻接链表，上次访问到了哪个位置
		int count = 0; //用于初始化LOW和DFN的
		int top = 0;
		
		DataVertex rootNode = this.root;
		Tmp firstTmp = new Tmp();
		firstTmp.head = -1;
		firstTmp.node = rootNode;
		firstTmp.pos = index;
		firstTmp.node.nrdfs_visited = true;
		firstTmp.node.DFN = count++;
		firstTmp.node.LOW = firstTmp.node.DFN;
		
		stk.push(firstTmp);
		top++; //标记stk的栈顶的，其实没多大用处
		stk2.push(rootNode);
		
		List<DataVertex> list = adjList.get(rootNode.id);
		
		//System.out.println("first step list is "+list);
		
		while(top > 0 || index < list.size()){
			
			while( index < list.size() ){
				
				DataVertex curVertex = list.get(index);
				
				//System.out.println("cur vertex is "+curVertex);
				
				if(curVertex.nrdfs_visited == true){
					
					//System.out.println("visited is true");
					
					if(curVertex.in_stk2 == true){
						
						//System.out.print("in stk2 is true, ");
						
						stk.peek().node.LOW = 
							Math.min(stk.peek().node.LOW, curVertex.DFN);
						
						//System.out.println("update stk peek as "+stk.peek().node);
					}
					
					index++;
				}
				else{
					
					//System.out.println("cur vertex visited is false");
					
					curVertex.nrdfs_visited = true;
					
					Tmp newTmp = new Tmp();
					newTmp.head = stk.peek().node.id;
					newTmp.node = curVertex;
					newTmp.pos = index;
					newTmp.node.DFN = count++;
					newTmp.node.LOW = newTmp.node.DFN;
					
					stk.push(newTmp);
					top++;
					stk2.push(curVertex);
					curVertex.in_stk2 = true;
					
					list = adjList.get(curVertex.id);
					
					//System.out.println("push "+curVertex+" and child list is "+list+"===================");
					
					index = 0;
					
				}
				
				
			}
			//System.out.println("index "+index +" >= list.size "+list.size());
			
			if(top > 0){
				
				//System.out.println("---------");
				//System.out.print("pop ");
				//stk的pop条件：
				//当发现叶子节点时候pop，因为叶子节点没有孩子，所以不满足上面while条件
				//当中间节点，他的孩子都遍历完了之后，也要pop
				//stk2的pop条件：
				//当stkpop的时候看一下stk2的栈顶是不是要pop
				top--;
				Tmp curTmp = stk.pop();
				
				//System.out.println(curTmp.node);
				//System.out.println("after pop stk has "+stk);
				list = adjList.get(curTmp.head);
				
				//System.out.println("cur list is "+list);
				
				if(list == null){
					break;
				}
				
				index = curTmp.pos+1; //加1表示n节点的邻接链表的下一个
				//假设u有多个孩子v1,v2,v3.现在是v2要出栈了，得更新u的low
				stk.peek().node.LOW = 
					Math.min(curTmp.node.LOW, stk.peek().node.LOW);
				
				//System.out.println("stk peek is "+stk.peek().node);
				
				//如果现在stk出栈的那个节点的low和dfn相等,stk2就发现了scc了
				if(curTmp.node.LOW == curTmp.node.DFN){
					
					//System.out.println(curTmp.node + "Low == DFN");
					DataVertex tmp2;
					SCC2 newSCC = new SCC2();
					do{
						tmp2 = stk2.pop();
						//tmp2 = stk2.get(stk.size()-1);
						//stk.remove(stk.size()-1);
						tmp2.in_stk2 = false;
						//System.out.println("pop "+tmp2);
						tmp2.SCCCorrespond = newSCC.id;
						newSCC.member.add(tmp2);
					}
					while(tmp2.id != curTmp.node.id);
					//System.out.println("this scc is "+newSCC.member+"+++++++++");
					
					if(newSCC.member.size() > 1){
						SCCList.add(newSCC);
					}
				}
			}
			
			
			
		}
		
		for(int i=0; i<SCCList.size(); i++){
			System.out.println("SCC size is "+SCCList.get(i).member.size());
		}
		
	}
	
	
	public void graphReconstruction(){
		
		//Iterator<DataVertex> it3,it4;
		int addEdgeNum = 0;
		
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
				
				//it3=this.getIncomingAdjacentVertices(curVertex).iterator();
				//it4=this.getOutgoingAdjacentVertices(curVertex).iterator();
				List<DataVertex> list3 = this.getIncomingAdjacentVertices(curVertex);
				List<DataVertex> list4 = this.getOutgoingAdjacentVertices(curVertex);
				
				DataVertex tmp;
				try {
					//while(it3.hasNext()){
					for(int k=0; k<list3.size(); k++){
						//tmp=it3.next();
						tmp = list3.get(k);
						if( (tmp.SCCCorrespond != curSCC.id) && this.getEdge(tmp, newSuperVertex)==null){	
							addEdgeNum++;
							this.addEdge(tmp, newSuperVertex);}
						this.removeEdge(this.getEdge(tmp, curVertex));
					}
					//while(it4.hasNext()){
					for(int k=0; k<list4.size(); k++){
						
						//tmp=it4.next();
						tmp = list4.get(k);
						if( (tmp.SCCCorrespond != curSCC.id) && this.getEdge(tmp, newSuperVertex)==null){		
							addEdgeNum++;
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
		System.out.println("graph reconstruction finished");
		System.out.println("The DAG has node num "+this.getVerticesCount());
		System.out.println("The DAG has node num "+this.idVertexMap.size());
		System.out.println("addEdgeNum "+addEdgeNum);
		
	}
	
	
	public static void main(String[] args){
		
		File xmlFile = new File("../data/xmark100/xmark0.xml");
		//File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");
		//File xmlFile=new File("auction.xml");
		DAG2 t = new DAG2(xmlFile);
		t.dfs2();
		t.graphReconstruction();
		
		
	}
}
