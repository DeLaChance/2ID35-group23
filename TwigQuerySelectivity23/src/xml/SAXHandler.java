/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import labeler.*;

/**
 * The Handler for SAX Events.
 */
class SAXHandler extends DefaultHandler {
    Graph g = new Graph();

    List<GraphNode> empList = new ArrayList<>();
    GraphNode emp = null;
    String content = null;
    int depth = -1;
    int prevChildId = 0;
    int history[][];
    public int maximum = 0;

    @Override
    //Triggered when the start of tag is found.
    public void startElement(String uri, String localName, 
                             String qName, Attributes attributes) 
                             throws SAXException {
      if(!qName.equals("incategory")) {
          depth++;
          if(depth > maximum) {
              maximum = depth;
          }
          System.out.println("Depth is one higher to: " + depth);
          System.out.println(qName);
          int childId = g.addNode(qName);
      } else {
          //If it is an incategory tag, which should be a custom edge.
          depth++;
          System.out.println("Depth is one higher to: " + depth);
          
      }

      /*switch(qName){
        //Create a new Employee object when the start tag is found
        case "employee":
          emp = new GraphNode();
          emp.id = attributes.getValue("id");
          break;
      }*/
    }

    @Override
    public void endElement(String uri, String localName, 
                           String qName) throws SAXException {
        depth--;
        System.out.println("Depth is one lower to: " + depth);
        System.out.println("End of element " + qName + " \n");
    }

    @Override
    public void characters(char[] ch, int start, int length) 
            throws SAXException {

    }
    
    public int getMaximum() {
        return maximum;
    }
}