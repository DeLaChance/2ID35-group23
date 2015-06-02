/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import labeler.*;

/**
 * The Handler for SAX Events.
 */
class SAXHandler extends DefaultHandler {
    
    public static String EDGE_IDENTIFIERS[] = { "incategory" };
    
    private Graph g = new Graph();
    private Stack st = new Stack();
    
    private int state = 0;
    private int counter = 0;
            
    public SAXHandler()
    {
    
    }
    
    public void nextPhase()
    {
        counter = -1;
        state += 1;
    }
    
    private boolean isEdgeIdentifier(String qName)
    {
        for(int i = 0; i < EDGE_IDENTIFIERS.length; i++)
        {
            if( qName.equals(EDGE_IDENTIFIERS[i]) )
            {
                return true;
            }
        }
        
        return false;
    }
            

    @Override
    //Triggered when the start of tag is found.
    public void startElement(String uri, String localName, 
                             String qName, Attributes attributes) 
                             throws SAXException 
    {
      if( state == 0 )
      {
        if(!this.isEdgeIdentifier(qName)) {

          int childId = g.addNode(qName);
          int length = attributes.getLength();
          
          for (int i=0; i<length; i++) 
          {
              String name = attributes.getQName(i);
              String value = attributes.getValue(i);

              g.getNode(childId).addAttribute(name, value);
              
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
      if( state == 1 )
      {
          if( this.isEdgeIdentifier(qName) )
          {
            int length = attributes.getLength();   
            for (int i=0; i<length; i++) 
            {
              String name = attributes.getQName(i);
              String value = attributes.getValue(i);
              
              // Name indicates the name of the tag that belongs to a node X
              // with which we want to connect
              // Value indicates the value of the id-attribute of X
              Integer k = g.findNode(name, value);
              
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
        if( state == 0 )
        {
            if(!this.isEdgeIdentifier(qName))
            {
                st.pop();
            }
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
}