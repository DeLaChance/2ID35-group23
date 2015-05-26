/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twigqueryselectivity23;



/**
 *
 * @author lucien
 */
public class TwigQuerySelectivity23 {

    // number of cells in x and y direction (total # cells: (HIST_GRID_WIDTH+1)*HIST_GRID_WIDTH/2 )
    public static final int HIST_GRID_WIDTH = 100;
    // number of bars in the equidepth histogram (rho = 1/HIST_CELL_BARS)
    public static final int HIST_CELL_BARS  =  10;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String fileLoc = "/home/lucien/Downloads/standard.xml" ;//args[0];
        //XMLParser xmlparser = new XMLParser(fileLoc);
    }
    
}
