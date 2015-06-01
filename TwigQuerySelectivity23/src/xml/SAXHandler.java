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

  List<GraphNode> empList = new ArrayList<>();
  GraphNode emp = null;
  String content = null;
  @Override
  //Triggered when the start of tag is found.
  public void startElement(String uri, String localName, 
                           String qName, Attributes attributes) 
                           throws SAXException {
      System.out.println(qName + "\n");
      
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
      System.out.println("End of element " + qName + " \n");
  }

  @Override
  public void characters(char[] ch, int start, int length) 
          throws SAXException {
    //content = String.copyValueOf(ch, start, length).trim();
  }
    
}