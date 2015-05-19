/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lucien
 */
public class Graph {
    
    private HashMap<Integer, Node> nodes = new HashMap<>();
    private HashMap<Integer, ArrayList<Edge>> inEdges = new HashMap<Integer, ArrayList<Edge>>();
    private HashMap<Integer, ArrayList<Edge>> outEdges = new HashMap<Integer, ArrayList<Edge>>();
    
    public Graph()
    {
    
    }
    
    public Graph(HashMap<Integer, Node> nodes, HashMap<Integer, ArrayList<Edge>> inEdges,
            HashMap<Integer, ArrayList<Edge>> outEdges)
    {
        this.nodes = nodes;
        this.inEdges = inEdges;
        this.outEdges = outEdges;
    }
    
    public void addNode(Node node)
    {
        if( !nodes.containsKey(node.getId()) )
        {
            nodes.put(node.getId(), node);
            
            inEdges.put(node.getId(), new ArrayList<>());
            outEdges.put(node.getId(), new ArrayList<>());
        }
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
    
    public Set<Integer> getNodes()
    {
        return this.nodes.keySet();
    }
    
    public Node getNode(Integer vkey)
    {
        if( this.nodes.containsKey(vkey) )
            return this.nodes.get(vkey);
        else
            return null;
    }
}
