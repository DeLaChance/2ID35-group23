package labeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Description
 */
public class Graph {
    
    private HashMap<Integer, Node> nodes = new HashMap<>();
    private HashMap<Integer, ArrayList<Edge>> inEdges = new HashMap<Integer, ArrayList<Edge>>();
    private HashMap<Integer, ArrayList<Edge>> outEdges = new HashMap<Integer, ArrayList<Edge>>();
    private int maxId = -1;
    
    public Graph()
    {
        this.nodes = new HashMap<Integer, Node>();
        this.inEdges = new HashMap<Integer, ArrayList<Edge>>();
        this.outEdges = new HashMap<Integer, ArrayList<Edge>>();
        this.maxId = -1;
    }
    
    //Constructor which sets the initial values of the Graph like the nodes and the edges.
    public Graph(HashMap<Integer, Node> nodes, HashMap<Integer, ArrayList<Edge>> inEdges,
            HashMap<Integer, ArrayList<Edge>> outEdges)
    {
        this.nodes = nodes;
        this.inEdges = inEdges;
        this.outEdges = outEdges;
    }
    
    //Add a node to the graph. Also the edges are added to this specific new node.
    public void addNode(Node node)
    {
        if( !nodes.containsKey(node.getId()) )
        {
            nodes.put(node.getId(), node);
            maxId += 1;
            
            inEdges.put(node.getId(), new ArrayList<>());
            outEdges.put(node.getId(), new ArrayList<>());
        }
    }
    
    public int addNode(String tag)
    {
        maxId += 1;
        Node node = new Node(tag, maxId);
        
        return maxId;
    }
    
    //Add @edge to the graph.
    public void addEdge(Integer from, Integer to)
    {
        Edge edge = new Edge(new Pair(from, to));
        this.addEdge(edge);
    }
    
    public void addEdge(Edge edge)
    {
        if( nodes.containsKey(edge.getLeft()) && nodes.containsKey(edge.getRight()) ) 
        {
            // Both nodes of the edge need to be present in the graph
            ArrayList<Edge> inE = inEdges.get(edge.getRight());
            ArrayList<Edge> outE = outEdges.get(edge.getLeft());
            
            inE.add(edge.reverse());
            outE.add(edge);
        }
    }
    
    //Returns the list of edges which goes to node with id @id
    public ArrayList<Edge> getInNeighbours(Integer id)
    {
        ArrayList<Edge> inE = new ArrayList<>();
        if( nodes.containsKey(id) )
        {
            Node node = nodes.get(id);
            inE = inEdges.get(id);            
        }
        
        return inE;
    }
    
    //Returns the list of edges which goes from node with id @id
    public ArrayList<Edge> getOutNeighbours(Integer id)
    {
        ArrayList<Edge> outE = new ArrayList<>();
        if( nodes.containsKey(id) )
        {
            Node node = nodes.get(id);
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
    public Node getNode(Integer vkey)
    {
        if( this.nodes.containsKey(vkey) )
            return this.nodes.get(vkey);
        else
            return null;
    }
}
