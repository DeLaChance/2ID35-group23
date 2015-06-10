/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import labeler.Graph;
import labeler.GraphEdge;
import labeler.GraphNode;
import labeler.Pair;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author lucien
 */
public class SAXHandler2 extends DefaultHandler {
    
    private Graph g = new Graph();
    private Stack st = new Stack();
    
    private int state = 0;
    private int counter = 0;
    private int size = 0;
    private HashMap<String, HashSet<String>> tagTypes;
    private HashMap<String, Boolean> isEdge;
    
    public SAXHandler2()
    {
        tagTypes = new HashMap<String, HashSet<String>>();
        isEdge = new HashMap<String, Boolean>();
    }
    
    public void nextPhase()
    {
        counter = -1;
        state += 1;
        
        if( state == 1 )
        {
            System.out.println("First round complete");
            System.out.println("Total number of tags: " + this.size);
            System.out.println("Number of distinct tags: " + tagTypes.keySet().size());
            buildIsEdgeMap();
        }
        if( state == 2 )
        {
            System.out.println("Second round complete");
        }
    }
    
    private void buildIsEdgeMap()
    {
        for(String s : tagTypes.keySet() )
        {
            HashSet<String> A = tagTypes.get(s);
            boolean b = false;
            for(String s1 : A)
            {
                b = (tagTypes.keySet().contains(s1));
            }
            
            isEdge.put(s, b);
        }
    }
    
    private boolean isEdge(String qName)
    {
        return isEdge.get(qName);
    }
    
       
   @Override
    //Triggered when the start of tag is found.
    public void startElement(String uri, String localName, 
                             String qName, Attributes attributes) 
                             throws SAXException 
    {
      if( state == 0 ) 
      {
        int length = attributes.getLength();
        if( ! tagTypes.containsKey(qName) )
        {
            tagTypes.put(qName, new HashSet<String>());
        }
        
        for (int i=0; i<length; i++) 
        {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);

            tagTypes.get(qName).add(name);
        }   
        
        size++;
      } 
        
      if( state == 1 )
      {
          if( !isEdge(qName) )
          {
            int childId = g.addNode(qName);
            int length = attributes.getLength();
            
            for (int i=0; i<length; i++) 
            {
                String name = attributes.getQName(i);
                String value = attributes.getValue(i);

                g.editNodeAttribute(childId, name, value);

            }

            if( !st.empty() )
            {
              Integer parentId = (Integer) st.lastElement();
              GraphNode parent = g.getNode( parentId );
              g.addEdge(parent.getId(), childId);
            }

            st.push(childId);
          }
      }
      
      if( state == 2 )
      {
        if( isEdge(qName) )
        {
            int length = attributes.getLength();   
            for (int i=0; i<length; i++) 
            {
              String name = attributes.getQName(i);
              String value = attributes.getValue(i);

              // Name indicates the name of the tag that belongs to a node X
              // with which we want to connect
              // Value indicates the value of the id-attribute of X
              Integer k = g.findNodeFast(name, value);

              if( k != -1 ) // found
              {
                  GraphEdge edge = new GraphEdge(new Pair(counter, k));
                  g.addEdge(edge);
              }
            }
        }
        else
        {
            counter++;
        }
      }

    }

    @Override
    public void endElement(String uri, String localName, 
                           String qName) throws SAXException 
    {
        if( state == 1 && !isEdge(qName) )
        {
            st.pop();
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) 
            throws SAXException {

    }
    
    public Graph getGraph()
    {
        return this.g;
    }    
    
    public HashMap<String, Boolean> getEdgeMap()
    {
        return this.isEdge;
    }
    
}
