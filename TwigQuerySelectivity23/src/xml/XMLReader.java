package xml;

import java.io.FileInputStream;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;

public class XMLReader {
    public XMLReader(double scalingfactor) throws Exception {
        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        SAXParser parser = parserFactor.newSAXParser();
        SAXHandler handler = new SAXHandler();

        parser.parse(new FileInputStream("../datasets/xmark-sf0.4.xml"), 
                     handler);
        System.out.println("Maximum depth: " + handler.getMaximum());
    }

  public static void main(String[] args) throws Exception {
    
    
    //Printing the list of employees obtained from XML
    /*for ( Employee emp : handler.empList){
      System.out.println(emp);
    }*/
  }
}