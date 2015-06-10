/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twigqueryselectivity23;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import labeler.Graph;
import labeler.Matrix;
import labeler.PrimeLabeler;
import labeler.Tarjan;
import xml.SAXHandler2;

/**
 *
 * @author lucien
 */
public class TestLabeling {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String XMLuri = "../datasets/xmark-sf0.4.xml"; //args[0];
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
            System.out.println("Parsing of graph done after: " + (System.currentTimeMillis()-timestamp) + "ms");
            System.out.println("|V|=" + g1.getNodes().size());
            System.out.println("|E|=" + g1.getNumberOfEdges());
            
            Graph g2 = Tarjan.createTarjanGraph(g1);
            System.out.println("Creation of Tarjan-graph: " + (System.currentTimeMillis()-timestamp) + "ms");
            
            HashMap<Integer, ArrayList<Integer>> A = PrimeLabeler.getPrimeLabeledGraph(g2);
            System.out.println("Prime-labeled graph: " + (System.currentTimeMillis()-timestamp) + "ms");
            
            Matrix M = new Matrix(A);
            System.out.println("Creation of matrix: " + (System.currentTimeMillis()-timestamp) + "ms");
            
            System.out.println();
            
            System.out.println("G'=(V',E')");           
            System.out.println(g2.toString());
            
            for(Integer I : A.keySet())
            {
                System.out.println(I + ": " + A.get(I));
            }
            
            System.out.println("Matrix M: ");
            M.print();

            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
