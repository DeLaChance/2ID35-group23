package xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import labeler.Graph;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLReader 
{
    private Graph g;
    private String url;
    
    
    public XMLReader(String url) 
    {
        this.url = url;
    }
    
    public void createGraph()
    {
        try 
        {
            SAXParserFactory parserFactor = SAXParserFactory.newInstance();
            SAXParser parser = parserFactor.newSAXParser();
            SAXHandler2 handler = new SAXHandler2();
            
            parser.parse(new FileInputStream(url), handler);
            handler.nextPhase();
            parser.parse(new FileInputStream(url), handler);
            handler.nextPhase();
            parser.parse(new FileInputStream(url), handler);        
            this.g = handler.getGraph();
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public Graph getGraph()
    {
        return this.g;
    }

}