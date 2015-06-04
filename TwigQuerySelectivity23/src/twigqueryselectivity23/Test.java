/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package twigqueryselectivity23;

import c1pGenerator.C1PMatrixGenerator;
import hist.Histogram;
import hist.debugView.MainView;

/**
 *
 * @author huib
 */
public class Test
{
    public static final void main(String[] args) throws Throwable
    {
        testHistogram();
    }
    
    public static void testHistogram()
    {
        Histogram hist = new Histogram(C1PMatrixGenerator.createMatrix());
        
        MainView mv = new MainView(hist);
    }
}
