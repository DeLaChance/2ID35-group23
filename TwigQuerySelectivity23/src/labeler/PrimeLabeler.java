package labeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class does the prime labeling part in the algorithm.
 */
public class PrimeLabeler {
    
    private static HashMap<Integer, ArrayList<Integer>> primeLabelperNode;
    private static Integer currentMax = -1;
    
    
    //This method generates the next prime number available and returns this.
    private static long genNextPrime(long n)
    {
        long nextPrime = n + 1;
        while(!isPrime(nextPrime))
        {
            nextPrime = n + 1;
        }
        
        return nextPrime;
    }
    
    //
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
    
    public static ArrayList<Integer> getTopologicalOrder(Graph G)
    {
        ArrayList<Integer> al = new ArrayList<Integer>();
        ArrayList<Integer> S = new ArrayList<Integer>();
        HashSet<Integer> isRemoved = new HashSet<Integer>();
        
        for(Integer vkey : G.getNodes() )
        {
            if( G.getInNeighbours(vkey).size() == 0)
            {
                S.add(vkey);              
            }
        }
        
        while(S.size() > 0)
        {
            Integer s = S.remove(0);
            if( !isRemoved.contains(s) )
            {
                isRemoved.add(s);
                al.add(s);
                ArrayList<Edge> outE = G.getOutNeighbours(s);

                for(Edge e : outE)
                {
                    Integer right = e.getRight();
                    ArrayList<Edge> inE = G.getInNeighbours(right);

                    boolean hasNoEdges = true;
                    for(Edge e2 : inE)
                    {
                        Integer right2 = e2.getRight();
                        if( !isRemoved.contains(right2) )
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
            
        }
        
        return al;
    }

    public static HashMap<Integer, ArrayList<Integer>> getPrimeLabeledGraph(Graph G)
    {
        primeLabelperNode = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> al = getTopologicalOrder(G);
        
        for(Integer vkey : G.getNodes())
        {
            primeLabelperNode.put(vkey, new ArrayList<Integer>());
        }
        
        // reverse topoligical order
        for(int i = al.size()-1; i >= 0; i--)
        {
            Integer vkey = al.get(i);
            
            // v is a leaf
            ArrayList<Edge> outE = G.getOutNeighbours(vkey);
            //if outE.size() == 0, than this edge is a leaf.
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
                    // n.l = n.l * c.l
                    Integer right = e.getRight();
                    ArrayList<Integer> leftVector = primeLabelperNode.get(vkey);
                    ArrayList<Integer> rightVector = primeLabelperNode.get(right);
                    ArrayList<Integer> productPrimeVector = productPrimeVector(leftVector, rightVector);
                    primeLabelperNode.put(vkey, productPrimeVector);    
                    
                    // Check for multiple parents
                    ArrayList<Edge> inE = G.getInNeighbours(right);
                    allHaveMultipleParents = allHaveMultipleParents && (inE.size() >= 2);
                }
                
                if( allHaveMultipleParents )
                {
                    currentMax += 1;
                    ArrayList<Integer> al1 = primeLabelperNode.get(vkey);
                    ArrayList<Integer> productPrimeVector = productPrimeVector(buildNewPrimeVector(), al1);
                    primeLabelperNode.put(vkey, productPrimeVector);
                }
            }
        }
        
        return primeLabelperNode;
    }
    
    private static ArrayList<Integer> buildNewPrimeVector()
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < currentMax; i++)
        {
            list.add(0);
        }
        
        list.add(1);
        
        return list;
    }

    private static ArrayList<Integer> productPrimeVector(ArrayList<Integer> al1, ArrayList<Integer> al2)
    {
        int max = Math.max(al1.size(), al2.size());
        int min = Math.min(al1.size(), al2.size());
        ArrayList<Integer> al3 = new ArrayList<Integer>();
        
        for(int i = 0; i < max; i++)
        {
            Integer j = 0;
            
            if(i < al1.size())
            {
                if(al1.get(i) == 1)
                {
                    j = 1;
                }
            }
            if( i < al2.size())
            {
                if(al2.get(i) == 1)
                {
                    j = 1;
                }
            }

            al3.add(j);

        }
        
        return al3;
    }
}
