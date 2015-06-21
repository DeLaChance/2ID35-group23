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


/**
 * TODO: Documentation
 */
public class Tarjan {
    
    static int index;
    //static HashMap<Integer, Triplet<Integer, Integer, Boolean>> nodeData;
    static HashMap<Integer, Integer> indexMap;
    static HashMap<Integer, Integer> lowLinkMap;
    static HashSet<Integer> onStack;
    static HashSet<Integer> isDefined;
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
        // Clear all variables
        reset();
        
        for(Integer node1 : G.getNodes() )
        {
            if( !isDefined.contains(node1) )
            {
                isDefined.add(node1);
                strongConnect(node1, G);
            }
        }
        
        return SCCs;
    }
    
    private static void reset()
    {
        index = 0;
        S = new Stack();
        isDefined = new HashSet<Integer>();
        indexMap = new HashMap<Integer, Integer>();
        lowLinkMap = new HashMap<Integer, Integer>();
        onStack = new HashSet<Integer>();   
        SCCs = new ArrayList<HashSet<Integer>>();
    }

    private static void strongConnect(Integer node1, Graph G)
    {  
        indexMap.put(node1, index);
        lowLinkMap.put(node1, index);
        index += 1;
        
        onStack.add(node1);
        S.push(node1);
        
        ArrayList<GraphEdge> outE = G.getOutNeighbours(node1);
        for(GraphEdge outEdge : outE )
        {
            Integer node2 = outEdge.getRight();
            
            if( !isDefined.contains(node2) )
            {
                isDefined.add(node2);
                strongConnect(node2, G);
                int minIndex = Math.min(lowLinkMap.get(node1), lowLinkMap.get(node2));
                lowLinkMap.put(node1, minIndex);
            }
            else
            {
                if( onStack.contains(node2) )
                {
                    int minIndex = Math.min(lowLinkMap.get(node1), indexMap.get(node2));
                    lowLinkMap.put(node1, minIndex);
                }
            }
        }
        
        if( indexMap.get(node1).equals(lowLinkMap.get(node1)) )
        {
            HashSet<Integer> SCC = new HashSet<Integer>();
            Integer node2 = null;
            
            do
            {
                node2 = (Integer) S.pop();
                onStack.remove(node2);
                SCC.add(node2);
            }
            while(node1 != node2);
            SCCs.add(SCC);
        }

    }
}
