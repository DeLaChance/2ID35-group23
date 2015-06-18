/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twigqueryselectivity23;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import labeler.Graph;
import labeler.GraphEdge;
import labeler.GraphNode;
import labeler.Matrix;
import labeler.Pair;
import labeler.PrimeLabeler;
import labeler.Tarjan;
import xml.SAXHandler2;

/**
 *
 * @author lucien
 */
public class TestLabeling2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
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

/*
// TODO code application logic here
        String XMLuri = "../datasets/xmark-sf0.001.xml"; //args[0];
        try
        {
            System.out.println("Input file: " + XMLuri);
            long timestamp = System.currentTimeMillis();
                    
            SAXParserFactory parserFactor = SAXParserFactory.newInstance();
            SAXParser parser = parserFactor.newSAXParser();
            SAXHandler2 handler = new SAXHandler2();
            
            parser.parse(new FileInputStream(XMLuri),
                    handler);
            handler.nextPhase();
            parser.parse(new FileInputStream(XMLuri),
                    handler);
            handler.nextPhase();
            parser.parse(new FileInputStream(XMLuri),
                    handler);
            Graph g1 = handler.getGraph();   
            
            Graph g2 = Tarjan.createTarjanGraph(g1);

            HashMap<Integer, Integer> A = g2.computeFrequenciesList();
            for(Integer key : A.keySet())
            {
                System.out.println(key + "|" + A.get(key));
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }*/
        
    }
    
}
