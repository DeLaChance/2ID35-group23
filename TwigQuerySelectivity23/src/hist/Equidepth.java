/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist;

import c1p.Position;

/**
 *
 * @author huib
 */
public class Equidepth
{
    private int barCount;
    private Bar[] bars = new Bar[barCount]; // ordered width ascending minX
    
    public Equidepth(int barCount)
    {
        this.barCount = barCount;
    }
    
    public double getBarCount()
    {
        return this.barCount;
    }
    
    protected void generateHistogram(PositionList datapoints)
    {
        int barSize = (datapoints.size()+barCount-1)/barCount;
        
    }
    
    public int getCount(int x, int y)
    {
        int count = 0;
        
        for(int i=0; i<bars.length; i++)
        {
            if(bars[i].maxX <= x && bars[i].minY <= y)
                count += barCount;
        }
        
        return count;
    }
    
    private class Bar
    {
        public int minX,maxX, minY,maxY;
    }
}
