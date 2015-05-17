import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import salvo.jesus.graph.Edge;


/**
 * This class describes the structure of a SCC.
 * All the functions about the operation on SCC are defined here!
 */
public class SCC{	
	protected DataGraph originalGraph;
	protected DataGraph scc;
	protected int level;
	protected int rootID;
	protected HashSet<Edge> entryEdges=null;
	protected HashSet<DataVertex> entryVertices=null;
	protected HashSet<Edge> exitEdges=null;
	protected HashSet<DataVertex> exitVertices=null;
	protected HashSet<DataVertex> entryExitVertices=null;
	protected HashSet<Integer> vertexIDSet=null;
	public SCC(){
		this.level=Integer.MAX_VALUE;
		this.entryEdges=new HashSet<Edge>();
		this.entryVertices=new HashSet<DataVertex>();
		this.exitEdges=new HashSet<Edge>();
		this.exitVertices=new HashSet<DataVertex>();
		this.vertexIDSet=new HashSet<Integer>();
		this.entryExitVertices=new HashSet<DataVertex>();
	}
	
	
	public String toString() {
	    return "["+"SCC"+": <"+this.vertexIDSet.size()+">]\n";
	}
}



