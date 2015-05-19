/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author lucien
 */
public class XMLParser {
    
    public XMLParser(String fileLoc) 
    {
        try
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            DefaultHandler handler = new XMLHandler();
            parser.parse(fileLoc, handler);
            
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
    }
    
}
