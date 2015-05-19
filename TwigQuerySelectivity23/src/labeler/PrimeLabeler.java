/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author lucien
 */
public class PrimeLabeler {
    
    private static HashMap<Integer, ArrayList<Integer>> primeLabelperNode;
    private static Integer currentMax = -1;
    
    
    private static long genNextPrime(long n)
    {
        long nextPrime = n + 1;
        while(!isPrime(nextPrime))
        {
            nextPrime = n + 1;
        }
        
        return nextPrime;
    }
    
    private static boolean isPrime(long n)
    {
        long bound = (long) Math.ceil(Math.sqrt(n));
        for(long i = 2; i < bound; i++)
        {
            if( n % i == 0 )
            {
                return false;
            }
        }
        
        return true;
    }
    
    private static ArrayList<Integer> getTopologicalOrder(Graph G)
    {
        ArrayList<Integer> al = new ArrayList<Integer>();
        ArrayList<Integer> S = new ArrayList<Integer>();
        HashSet<Edge> isRemoved = new HashSet<Edge>();
        
        for(Integer vkey : G.getNodes() )
        {
            if( G.getInNeighbours(vkey).size() == 0)
            {
                S.add(vkey);
            }
        }
        
        while(S.size() > 0)
        {
            Integer s = S.get(0);
            al.add(s);
            ArrayList<Edge> outE = G.getOutNeighbours(s);
            
            for(Edge e : outE)
            {
                isRemoved.add(e);
                Integer right = e.getRight();
                ArrayList<Edge> inE = G.getInNeighbours(right);
                
                boolean hasNoEdges = true;
                for(Edge e2 : inE)
                {
                    if( !isRemoved.contains(e) )
                    {
                        hasNoEdges = false;
                    }
                }
                
                if( hasNoEdges )
                {
                    S.add(right);
                }
            }
            
            
        }
        
        return al;
    }
    
    public static void getPrimeLabeledGraph(Graph G)
    {
        primeLabelperNode = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> al = getTopologicalOrder(G);
        
        // reverse topoligical order
        for(int i = al.size()-1; i > 0; i--)
        {
            Integer vkey = al.get(i);
            
            // v is a leaf
            ArrayList<Edge> outE = G.getOutNeighbours(vkey);
            if( outE.size() == 0 )
            {
                currentMax += 1;
                primeLabelperNode.put(vkey, buildNewPrimeVector());
            }
            else
            {
                boolean allHaveMultipleParents = true;
                for(Edge e : outE)
                {
                    Integer right = e.getRight();
                    
                    
                }
                
                if( allHaveMultipleParents )
                {
                
                }
            }
        }
    }
    
    private static ArrayList<Integer> buildNewPrimeVector()
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < currentMax-1; i++)
        {
            list.add(0);
        }
        
        list.add(1);
        
        return list;
    }
    
    private ArrayList<Integer> productPrimeVector(ArrayList<Integer> al1, ArrayList<Integer> al2)
    {
        int j = Math.max(al1.size(), al2.size());
        for(int i = 0; i < j; i++)
        {
            
        }
    }
    
}
