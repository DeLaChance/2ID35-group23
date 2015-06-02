package labeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Description
 */
public class Graph {
    
    private HashMap<Integer, GraphNode> nodes = new HashMap<>();
    private HashMap<Integer, ArrayList<GraphEdge>> inEdges = new HashMap<Integer, ArrayList<GraphEdge>>();
    private HashMap<Integer, ArrayList<GraphEdge>> outEdges = new HashMap<Integer, ArrayList<GraphEdge>>();
    private int maxId = -1;
    
    public Graph()
    {
        this.nodes = new HashMap<Integer, GraphNode>();
        this.inEdges = new HashMap<Integer, ArrayList<GraphEdge>>();
        this.outEdges = new HashMap<Integer, ArrayList<GraphEdge>>();
        this.maxId = -1;
    }
    
    //Constructor which sets the initial values of the Graph like the nodes and the edges.
    public Graph(HashMap<Integer, GraphNode> nodes, HashMap<Integer, ArrayList<GraphEdge>> inEdges,
            HashMap<Integer, ArrayList<GraphEdge>> outEdges)
    {
        this.nodes = nodes;
        this.inEdges = inEdges;
        this.outEdges = outEdges;
    }
    
    //Add a node to the graph. Also the edges are added to this specific new node.
    public void addNode(GraphNode node)
    {
        if( !nodes.containsKey(node.getId()) )
        {
            nodes.put(node.getId(), node);
            maxId += 1;
            
            inEdges.put(node.getId(), new ArrayList<>());
            outEdges.put(node.getId(), new ArrayList<>());
        }
    }
    
    public int addNode(String tag, int depth)
    {
        maxId += 1;
        GraphNode node = new GraphNode(tag, maxId, depth);
        
        return maxId;
    }
    
    //Add @edge to the graph.
    public void addEdge(Integer from, Integer to)
    {
        GraphEdge edge = new GraphEdge(new Pair(from, to));
        this.addEdge(edge);
    }
    
    public void addEdge(GraphEdge edge)
    {
        if( nodes.containsKey(edge.getLeft()) && nodes.containsKey(edge.getRight()) ) 
        {
            // Both nodes of the edge need to be present in the graph
            ArrayList<GraphEdge> inE = inEdges.get(edge.getRight());
            ArrayList<GraphEdge> outE = outEdges.get(edge.getLeft());
            
            inE.add(edge.reverse());
            outE.add(edge);
        }
    }
    
    //Returns the list of edges which goes to node with id @id
    public ArrayList<GraphEdge> getInNeighbours(Integer id)
    {
        ArrayList<GraphEdge> inE = new ArrayList<>();
        if( nodes.containsKey(id) )
        {
            GraphNode node = nodes.get(id);
            inE = inEdges.get(id);            
        }
        
        return inE;
    }
    
    //Returns the list of edges which goes from node with id @id
    public ArrayList<GraphEdge> getOutNeighbours(Integer id)
    {
        ArrayList<GraphEdge> outE = new ArrayList<>();
        if( nodes.containsKey(id) )
        {
            GraphNode node = nodes.get(id);
            outE = outEdges.get(id);            
        }
        
        return outE;
    }
    
    //Returns all nodes of the graph.
    public Set<Integer> getNodes()
    {
        return this.nodes.keySet();
    }
    
    //Returns a node with id @vkey
    public GraphNode getNode(Integer vkey)
    {
        if( this.nodes.containsKey(vkey) )
            return this.nodes.get(vkey);
        else
            return null;
    }
}
