/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author lucien
 */
class XMLHandler extends DefaultHandler {

    XMLHandler() {

    }

 @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
//        System.out.println("Start Element :" + qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    }

    // To take specific actions for each chunk of character data (such as
    // adding the data to a node or buffer, or printing it to a file).
    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
    }
    
}
