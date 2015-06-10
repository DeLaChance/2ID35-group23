/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package twigqueryselectivity23;

import c1p.C1PMatrix;
import c1p.C1PRow;
import c1pGenerator.C1PMatrixGenerator;
import hist.Histogram;
import hist.debugView.MainView;
import java.util.List;

/**
 *
 * @author huib
 */
public class Test
{
    public static final void main(String[] args) throws Throwable
    {
		testMatrixGenerator();
        testHistogram();
    }
    
	public static void testMatrixGenerator()
	{
		C1PMatrix m = C1PMatrixGenerator.createMatrix();
		System.out.println(m.toString());
	}
	
    public static void testHistogram()
    {
        Histogram hist = new Histogram(C1PMatrixGenerator.createMatrix());
        
        MainView mv = new MainView(hist);
    }
}
