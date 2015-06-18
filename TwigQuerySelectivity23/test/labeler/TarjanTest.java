/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labeler;

import java.util.ArrayList;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lucien
 */
public class TarjanTest {
    
    public TarjanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of runTarjan method, of class Tarjan.
     */
    @Test
    public void testRunTarjan() {
        GraphNode[] nodes = new GraphNode[8];
        for(int i = 0; i < 8; i++)
            nodes[i] = new GraphNode(null, i+1);
        
        GraphEdge[] edges = new GraphEdge[16];
        edges[0] = new GraphEdge(new Pair(1,2));
        edges[1] = new GraphEdge(new Pair(2,3));
        edges[2] = new GraphEdge(new Pair(3,1));
        edges[3] = new GraphEdge(new Pair(4,2));
        edges[4] = new GraphEdge(new Pair(4,3));
        edges[5] = new GraphEdge(new Pair(4,5));
        edges[6] = new GraphEdge(new Pair(5,4));
        edges[7] = new GraphEdge(new Pair(5,6));
        edges[8] = new GraphEdge(new Pair(6,3));
        edges[9] = new GraphEdge(new Pair(5,6));
        edges[10] = new GraphEdge(new Pair(6,3));
        edges[11] = new GraphEdge(new Pair(6,7));
        edges[12] = new GraphEdge(new Pair(7,6));
        edges[13] = new GraphEdge(new Pair(8,7));
        edges[14] = new GraphEdge(new Pair(8,5));
        edges[15] = new GraphEdge(new Pair(8,8));
        
        Graph G = new Graph();
        for(GraphNode n : nodes)
        {
            G.addNode(n);
        }
        for(GraphEdge e : edges)
        {
            G.addEdge(e);
        }
        
        ArrayList<HashSet<Integer>> SCCs = Tarjan.runTarjan(G);
        assertEquals(SCCs.size(), 4);
        
        Graph G2 = Tarjan.createTarjanGraph(G);
        assertEquals(G2.getNodes().size(), 4);
        assertEquals(G2.getOutNeighbours(1).size(), 1);
        assertEquals(G2.getOutNeighbours(3).size(), 2);
    }
  
    @Test    
    public void testRunTarjan2() {
        GraphNode[] nodes = new GraphNode[10];
        for(int i = 0; i < 10; i++)
            nodes[i] = new GraphNode(null, i);
        
        GraphEdge[] edges = new GraphEdge[12];
        edges[0] = new GraphEdge(new Pair(2,0));
        edges[1] = new GraphEdge(new Pair(2,1));
        edges[2] = new GraphEdge(new Pair(2,3)); 
        edges[3] = new GraphEdge(new Pair(3,4));
        edges[4] = new GraphEdge(new Pair(4,2));
        edges[5] = new GraphEdge(new Pair(3,5));
        edges[6] = new GraphEdge(new Pair(5,6));
        edges[7] = new GraphEdge(new Pair(6,7));
        edges[8] = new GraphEdge(new Pair(7,3));
        edges[9] = new GraphEdge(new Pair(4,8));
        edges[10] = new GraphEdge(new Pair(8,9));
        edges[11] = new GraphEdge(new Pair(4,9));
        
        Graph G = new Graph();
        for(GraphNode n : nodes)
        {
            G.addNode(n);
        }
        for(GraphEdge e : edges)
        {
            G.addEdge(e);
        }
        
        ArrayList<HashSet<Integer>> SCCs = Tarjan.runTarjan(G);
        
        for(int i = 0; i < SCCs.size(); i++)
        {
            System.out.println(SCCs.get(i));
        }
        
        Graph G2 = Tarjan.createTarjanGraph(G);
        G2.print();
        
        assertEquals(SCCs.size(), 5);
        assertEquals(G2.getNodes().size(), 4);
        
    }    
    
    
    
}
