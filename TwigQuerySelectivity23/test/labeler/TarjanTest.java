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
        Node[] nodes = new Node[8];
        for(int i = 0; i < 8; i++)
            nodes[i] = new Node(null, i+1);
        
        Edge[] edges = new Edge[16];
        edges[0] = new Edge(new Pair(1,2));
        edges[1] = new Edge(new Pair(2,3));
        edges[2] = new Edge(new Pair(3,1));
        edges[3] = new Edge(new Pair(4,2));
        edges[4] = new Edge(new Pair(4,3));
        edges[5] = new Edge(new Pair(4,5));
        edges[6] = new Edge(new Pair(5,4));
        edges[7] = new Edge(new Pair(5,6));
        edges[8] = new Edge(new Pair(6,3));
        edges[9] = new Edge(new Pair(5,6));
        edges[10] = new Edge(new Pair(6,3));
        edges[11] = new Edge(new Pair(6,7));
        edges[12] = new Edge(new Pair(7,6));
        edges[13] = new Edge(new Pair(8,7));
        edges[14] = new Edge(new Pair(8,5));
        edges[15] = new Edge(new Pair(8,8));
        
        Graph G = new Graph();
        for(Node n : nodes)
        {
            G.addNode(n);
        }
        for(Edge e : edges)
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
    
    
    
}
