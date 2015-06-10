/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twigqueryselectivity23;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import xml.XMLReader;



/**
 *
 * @author lucien
 */
public class TwigQuerySelectivity23 {

    // number of cells in x and y direction (total # cells: (HIST_GRID_WIDTH+1)*HIST_GRID_WIDTH/2 )
    public static final int HIST_GRID_WIDTH = 100;
    // number of bars in the equidepth histogram (rho = 1/HIST_CELL_BARS)
    public static final int HIST_CELL_BARS  =  10;
    // number of columns in randomly generated C1P matrix
    public static final int GEN_COLUMN_COUNT = 100000000;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, Exception {
        // TODO code application logic here
        //String fileLoc = "/home/lucien/Downloads/standard.xml" ;//args[0];
        try {
            //XMLReader xmlparser = new XMLReader(0.4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
