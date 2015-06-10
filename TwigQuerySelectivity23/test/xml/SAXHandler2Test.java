/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import labeler.Graph;
import labeler.GraphEdge;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author lucien
 */
public class SAXHandler2Test {
    
    public SAXHandler2Test() {
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
     * Test of getGraph method, of class SAXHandler2.
     */
    @Test
    public void testGetGraph() {
        try {
            SAXParserFactory parserFactor = SAXParserFactory.newInstance();
            SAXParser parser = parserFactor.newSAXParser();
            SAXHandler2 handler = new SAXHandler2();
            
            parser.parse(new FileInputStream("../datasets/saxreadertest.xml"),
                    handler);
            handler.nextPhase();
            parser.parse(new FileInputStream("../datasets/saxreadertest.xml"),
                    handler);
            handler.nextPhase();
            parser.parse(new FileInputStream("../datasets/saxreadertest.xml"),
                    handler);
            Graph g = handler.getGraph();
            
            ArrayList<GraphEdge> a = g.getOutNeighbours(0);
            ArrayList<GraphEdge> b = g.getInNeighbours(0);
            
            assertTrue(a.size() == 4);
            //assertTrue(a.get(0).getRight() == 1 && a.get(1).getRight() == 2 && a.get(2).getRight() == 3);
            assertTrue(b.size() == 1);
            //assertTrue(b.get(0).getRight() == )
            
            System.out.println(g.toString());
        } catch (Exception ex) {
            Logger.getLogger(SAXHandler2Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
