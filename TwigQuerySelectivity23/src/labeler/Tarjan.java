/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import org.javatuples.Triplet;

/**
 * TODO: Documentation
 */
public class Tarjan {
    
    static int index;
    static HashMap<Integer, Triplet<Integer, Integer, Boolean>> nodeData;
    static Stack S;
    static ArrayList<HashSet<Integer>> SCCs;
    
    //Generates a Tarjan Graph from a given Graph @G
    public static Graph createTarjanGraph(Graph G)
    {
        ArrayList<HashSet<Integer>> SCCs = runTarjan(G);
        Graph G2 = new Graph();
        HashMap<Integer, ArrayList<Integer>> edges = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
        int j = 0;
        
        for(HashSet<Integer> SCC : SCCs )
        {
            GraphNode n = new GraphNode(null,j);
            nodes.add(n);
            System.out.println("SCC-" + j + " contains " + SCC.toString());
            edges.put(j, new ArrayList<Integer>());
            j++;
        }
        
        j = 0;
        for(HashSet<Integer> SCC : SCCs )
        {
            for(Integer vkey : SCC)
            {
                ArrayList<GraphEdge> outE = G.getOutNeighbours(vkey);
                for(GraphEdge e : outE)
                {
                    Integer wkey = e.getRight();
                    
                    int l = -1;
                    int l2 = 0;
                    for(HashSet<Integer> SCC2 : SCCs )
                    {
                        if( !SCC2.equals(SCC) && SCC2.contains(wkey) )
                        {
                            l = l2;
                            break;
                        }
                        
                        l2++;
                    }

                    if( j != l && l != -1 )
                    {
                        ArrayList<Integer> outList = edges.get(j);
                        if( !outList.contains(l) )
                            outList.add(l);
                    }
                }
            }
            
            j++;
        }
        
        for(GraphNode n : nodes)
        {
            G2.addNode(n);
        }
        
        for(Integer key : edges.keySet())
        {
            ArrayList<Integer> E = edges.get(key);
            for(Integer key2 : E)
            {
                GraphEdge edge = new GraphEdge(new Pair(key,key2));
                G2.addEdge(edge);
            }

        }
        
        return G2;
    }
    
    public static ArrayList<HashSet<Integer>> runTarjan(Graph G)
    {
        index = 0;
        S = new Stack();
        // index, lowlink, onstack
        nodeData = new HashMap<Integer, Triplet<Integer, Integer, Boolean>>();
        SCCs = new ArrayList<HashSet<Integer>>();
        
        for(Integer vkey : G.getNodes())
        {
            if( !nodeData.containsKey(vkey) )
            {
                strongConnect(vkey, G);
            }
        }
        
        return SCCs;
    }
    
    private static void strongConnect(Integer vkey, Graph G)
    {
        if( !nodeData.containsKey(vkey) )
        {
            nodeData.put(vkey, new Triplet(index, index, true));
        }
        else
        {
            nodeData.remove(vkey);
            nodeData.put(vkey, new Triplet(index, index, true));
        }
        
        index += 1;
        S.push( vkey );
        
        ArrayList<GraphEdge> outE = G.getOutNeighbours(vkey);
        for(GraphEdge e : outE)
        {
            Integer wkey = e.getRight();
            if( !nodeData.containsKey(wkey) )
            {
                strongConnect(wkey, G);
                Integer minLink = Math.min(nodeData.get(vkey).getValue1(), 
                    nodeData.get(wkey).getValue1());
                updateLL(vkey, minLink);
            }
            else
            {
                if( nodeData.get(wkey).getValue2() )
                {
                    Integer minLink = Math.min(nodeData.get(vkey).getValue1(), 
                        nodeData.get(wkey).getValue0());                
                    updateLL(vkey, minLink);
                }
            }
        }
        
        if( nodeData.get(vkey).getValue0() == nodeData.get(vkey).getValue1() && 
            nodeData.containsKey(vkey) )
        {
            HashSet<Integer> SCC = new HashSet<Integer>();
            
            Integer k = (Integer) S.pop();
            while( k != vkey )
            {
                updateOS(k, false);
                SCC.add(k);
                k = (Integer) S.pop();
            }
            SCC.add(k);
            SCCs.add(SCC);
        }
    }
    
    private static void updateIndex(Integer k, Integer i)
    {
        Triplet<Integer, Integer, Boolean> t = new Triplet(i,nodeData.get(k).getValue1(),nodeData.get(k).getValue2());
        nodeData.remove(k);
        nodeData.put(k, t);
    }
    
    private static void updateLL(Integer k, Integer i)
    {
        Triplet<Integer, Integer, Boolean> t = new Triplet(nodeData.get(k).getValue0(),i,nodeData.get(k).getValue2());
        nodeData.remove(k);
        nodeData.put(k, t);
    }    
    
    private static void updateOS(Integer k, Boolean i)
    {
        Triplet<Integer, Integer, Boolean> t = new Triplet(nodeData.get(k).getValue0(),nodeData.get(k).getValue1(),i);
        nodeData.remove(k);
        nodeData.put(k, t);
    } 
    
}
