/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labeler;

import java.util.ArrayList;
import java.util.HashMap;
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
public class MatrixTest {
    
    public MatrixTest() {
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
     * Test of getMatrix method, of class Matrix.
     */
    @Test
    public void testGetMatrix() {
        Node[] nodes = new Node[6];
        for(int i = 0; i < 6; i++)
            nodes[i] = new Node(null, i);
        
        Edge[] edges = new Edge[7];
        edges[0] = new Edge(new Pair(0,1));
        edges[1] = new Edge(new Pair(0,2));
        edges[2] = new Edge(new Pair(1,3));
        edges[3] = new Edge(new Pair(1,4));
        edges[4] = new Edge(new Pair(2,3));
        edges[5] = new Edge(new Pair(2,4));
        edges[6] = new Edge(new Pair(5,2));
        
        Graph G = new Graph();
        
        for(Node n : nodes)
        {
            G.addNode(n);
        }
        
        for(Edge e : edges)
        {
            G.addEdge(e);
        }
        
        HashMap<Integer, ArrayList<Integer>> A = PrimeLabeler.getPrimeLabeledGraph(G);
        Matrix m = new Matrix(A);
        boolean a = m.toString().startsWith("[1, 1, 1, 1, 0]") || m.toString().startsWith("[1, 1, 1, 0, 1]");
        assertTrue(a);
    }
    
}