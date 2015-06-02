/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package c1pGenerator;

import c1p.C1PMatrix;
import c1p.C1PRow;

/**
 *
 * @author huib
 */
public class C1PMatrixGenerator
{
    public static C1PMatrix createMatrix()
    {
        C1PMatrix m = new C1PMatrix();
        int max = 512;
        
        for(int i=0; i<1024; i++)
        {
            int x,y;
            do
            {
                x = randInt(0,max);
                y = randInt(0,max);
            }
            while(!(x<y));
            
            m.add(new C1PRow(null, x, y));
        }
        
        return m;
    }
    
    // random int in range [min, max) 
    // i.e. including min, excluding max
    private static int randInt(int min, int max)
    {
        assert min < max;
        
        return min+(int)((max-min)*Math.random());
    }
}
