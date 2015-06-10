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
    private HashMap<String, ArrayList<Integer>> customIds = new HashMap<>();
    private HashMap<Integer, ArrayList<GraphEdge>> inEdges = new HashMap<Integer, ArrayList<GraphEdge>>();
    private HashMap<Integer, ArrayList<GraphEdge>> outEdges = new HashMap<Integer, ArrayList<GraphEdge>>();
    private int maxId = 0;
    private int noOfEdges = 0;
    
    public Graph()
    {
        this.nodes = new HashMap<Integer, GraphNode>();
        this.inEdges = new HashMap<Integer, ArrayList<GraphEdge>>();
        this.outEdges = new HashMap<Integer, ArrayList<GraphEdge>>();
        this.customIds = new HashMap<String, ArrayList<Integer>>();
        this.maxId = 0;
        this.noOfEdges = 0;
    }
    
    //Constructor which sets the initial values of the Graph like the nodes and the edges.
    public Graph(HashMap<Integer, GraphNode> nodes, HashMap<Integer, ArrayList<GraphEdge>> inEdges,
            HashMap<Integer, ArrayList<GraphEdge>> outEdges)
    {
        this.nodes = nodes;
        this.inEdges = inEdges;
        this.outEdges = outEdges;
        this.noOfEdges = (inEdges.size() + outEdges.size()) / 2;
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
            
            if( node.getCustomId() != null )
            {
                String s = node.getCustomId();
                ArrayList<Integer> L = this.customIds.get(s);
                if( L == null )
                {
                    this.customIds.put(s, new ArrayList<Integer>());
                }
                
                L.add(node.getId());
                
            }
            
            //System.out.println("Added node: " + node.getId() + " with tag " + node.getTag());
        }
    }

    public int addNode(String tag)
    {
        GraphNode node = new GraphNode(tag, maxId, new HashMap<String, String>());
        addNode(node);
        
        return maxId-1;   
    }
    
    public int addNode(String tag, HashMap<String, String> attributes)
    {
        GraphNode node = new GraphNode(tag, maxId, attributes);
        addNode(node);
        
        return maxId-1;
    }
    
    public void editNodeAttribute(Integer key, String attribute, String value)
    {
        if( this.nodes.containsKey(key) )
        {
            GraphNode node = this.nodes.get(key);
            node.addAttribute(attribute, value);
            if( node.getCustomId() != null )
            {
                String s = node.getCustomId();
                ArrayList<Integer> L = this.customIds.get(s);
                if( L == null )
                {
                    L = new ArrayList<Integer>();
                    this.customIds.put(s, L);
                }
                
                L.add(node.getId());
                
            }        
        }
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
            
            noOfEdges += 1;
            
            //System.out.println("Added edge: ( " + edge.getLeft() + ", " 
            //    + edge.getRight() + " )");            
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
    
    public Integer findNode(String tag, String tagid)
    {
        for(Integer key : this.nodes.keySet())
        {
            GraphNode node = this.nodes.get(key);
            if( node.getAttributeByName("id") != null )
            {
                if( node.getTag().equals(tag) && node.getAttributeByName("id").
                    equals(tagid))
                {
                    return node.getId();
                }
            }
        }
        
        return -1;
    }
    
    public Integer findNodeFast(String tag, String tagid)
    {
        if( this.customIds.containsKey(tagid))
        {
            for(Integer I : this.customIds.get(tagid))
            {
                GraphNode node = this.nodes.get(I);
                if( node.getTag().equals(tag) && node.getAttributeByName("id").
                    equals(tagid))
                {
                    return node.getId();
                }
            }
        }
        
        return -1;
    }
    
    public String toString()
    {
        String a = "[";
        for(Integer key : this.nodes.keySet())
        {
            a += key + ": [";
            
            if( this.outEdges.get(key).size() > 0 )
            {
                for(GraphEdge e : this.outEdges.get(key) )
                {
                    a += "(" + e.getLeft() + "," + e.getRight() + "), ";
                }
            }
            
            a += "] \n ";
        }
        
        if( this.nodes.keySet().size() > 0)
            a = a.substring(0, a.length()-2);
        
        a += "]";
        
        return a;
    }
    
    public void print()
    {
        for(Integer key : this.nodes.keySet())
        {
            System.out.println(key + ": " + this.outEdges.get(key).toString());
        }
    }
    

    public int getNumberOfEdges()
    {
        return this.noOfEdges;
    }
}
