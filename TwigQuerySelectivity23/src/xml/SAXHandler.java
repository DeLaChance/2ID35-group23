/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.util.ArrayList;
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
    Graph g = new Graph();

    GraphNode emp = null;
    int depth = -1;
    int prevChildId = 0;
    //The real stack of nodes, used to build a graph.
    Stack st = new Stack();
    //Used to move nodes.
    Stack intermediate = new Stack();

    @Override
    //Triggered when the start of tag is found.
    public void startElement(String uri, String localName, 
                             String qName, Attributes attributes) 
                             throws SAXException {
      if(!qName.equals("incategory")) {
          depth++;
          System.out.println("Depth is one higher to: " + depth);
          System.out.println(qName);
          int childId = g.addNode(qName, depth);
          
          GraphNode parent = (GraphNode) st.pop();
          g.addEdge(parent.getId(), childId);
          st.push(parent);
          st.push(g.getNode(childId));
          
          /*boolean isParent = false;
          Looking for the parent of childId
          while(!isParent && !st.empty()) {
              GraphNode parent = (GraphNode) st.pop();
              intermediate.push(parent);
              if(depth == parent.getDepth()+1) {
                  g.addEdge(parent.getId(), childId);
                  isParent = true;
              }
          }
          
          //Put everything back on the stack and add the new node to the stack.
          while(!intermediate.empty()) {
              GraphNode lastNode = (GraphNode) intermediate.pop();
              st.push(lastNode);
          }*/
          
      } else {
          //If it is an incategory tag, which should be a custom edge.
          depth++;
          System.out.println("Incategory tag, not handled in SAXHandler");
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
        st.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) 
            throws SAXException {

    }
}