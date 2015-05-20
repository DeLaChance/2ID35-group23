package labeler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * 
 * This class returns a binary matrix that can be used as input for the C1P-
 * phase.
 * 
 * The output is a binary MxN matrix that is sorted (descending order) on the number
 * of 1's per row.
 * 
 */
public class Matrix {
    
    private ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();
    private HashMap<Integer, Integer> mappingNodeToRowIndex = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mappingRowToNodeIndex = new HashMap<Integer, Integer>();
    private long N, M;
    
    public Matrix(HashMap<Integer, ArrayList<Integer>> plpn) 
    {
        matrix = new ArrayList<ArrayList<Integer>>();
        mappingNodeToRowIndex = new HashMap<Integer, Integer>();
        mappingRowToNodeIndex = new HashMap<Integer, Integer>();
        ArrayList<Pair<Integer, Integer>> No1PerRow = new ArrayList<Pair<Integer, Integer>>();
        this.N = 0;
        this.M = plpn.keySet().size();
        
        // Add all rows
        int index = 0;
        for(Integer key : plpn.keySet())
        {
            ArrayList<Integer> L = plpn.get(key);
            if( L.size() > N )
            {
                N = L.size();
            }
            
            mappingNodeToRowIndex.put(key, index);
            mappingRowToNodeIndex.put(index, key);
            matrix.add(L);
            index++;
        }
        
        // Make all rows of the same length and count number of 1's
        index = 0;
        for(ArrayList<Integer> L : matrix)
        {
            int numberofones = 0;
            for(int i = 0; i < N; i++)
            {
                if( i >= 0 && i < L.size() )
                {
                    if( L.get(i) == 1 )
                    {
                        numberofones++;
                    }
                }
                
                if( i >= L.size() )
                {
                    L.add(0);
                }
            }
            
            Integer vkey = mappingRowToNodeIndex.get(index);
            No1PerRow.add( new Pair(vkey, numberofones) );
            index++;
        }
        
        // Sort rows by number of 1's
        sort(No1PerRow);
        for(int i = No1PerRow.size()-1; i >= 0; i--)
        {
            Pair<Integer,Integer> pair = No1PerRow.get(i);
            Integer key = pair.getFirst();
            
            Integer rold = mappingNodeToRowIndex.get(key);
            int rnew = (No1PerRow.size()-1) - i;

            // Update mappings           
            swapRows(rold, rnew);
            
        }

    }
    
    /**
     * Swaps two rows of the matrix, updating the mappings from
     * node key to row index in the matrix and vice versa
     * 
     * @param rold
     * @param rnew 
     */
    public void swapRows(int rold, int rnew)
    {
        Integer oldkey = mappingRowToNodeIndex.get(rnew);
        Integer key = mappingRowToNodeIndex.get(rold);
        
        mappingNodeToRowIndex.put(oldkey, rold);
        mappingNodeToRowIndex.put(key, rnew);      
        mappingRowToNodeIndex.put(rold, oldkey);
        mappingRowToNodeIndex.put(rnew, key);

        // Swap rows
        ArrayList<Integer> L1 = matrix.get(rnew);
        ArrayList<Integer> L2 = matrix.get(rold);
        ArrayList<Integer> R = L1;       

        matrix.set(rnew, L2);
        matrix.set(rold, R);    
    }
    
    public ArrayList<ArrayList<Integer>> getMatrix()
    {
        return this.matrix;
    }
    
    private void sort(ArrayList<Pair<Integer, Integer>> l) 
    {
      // check for empty or null array
      if (l ==null || l.size()==0){
        return;
      }

      quickSort(0, l.size() - 1, l);
    }    
    
    private void quickSort(int low, int high, ArrayList<Pair<Integer, Integer>> L)
    {
        int i = low;
        int j = high;
        int pivot = L.get(low + (high-low)/ 2).getSecond();
        
        while(i <= j)
        {
            while(L.get(i).getSecond() < pivot)
            {
                i++;
            }
            while(L.get(j).getSecond() > pivot)
            {
                j--;
            }
        
            if(i <= j)
            {
                Pair<Integer, Integer> r = L.get(i);
                L.set(i, L.get(j));
                L.set(j, r);
                i++;
                j--;
            }
        }
        
        if(low < j)
        {
            quickSort(low, j, L);
        }
        if(i < high)
        {
            quickSort(i, high, L);
        }
    }
    
    public String toString()
    {
        String s = "";
        for(ArrayList<Integer> L : this.matrix)
        {
            s += L.toString() + "\n";
        }
        
        return s;
    }
    
}
