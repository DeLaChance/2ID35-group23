/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist;

/**
 *
 * @author huib
 */
public class Equidepth
{
    private int barCount;
    private int cellCount = 0;
    private int left=Integer.MAX_VALUE, upp=Integer.MIN_VALUE;
    private Bar[] bars = new Bar[barCount]; // ordered width ascending minX
    
    public Equidepth(int barCount)
    {
        this.barCount = barCount;
    }
    
    public double getBarCount()
    {
        return this.barCount;
    }
    
    protected Bar[] getBars()
    {
        return this.bars;
    }
    
    protected void rebuild(PositionList<? extends Position> datapoints)
    {
        cellCount = datapoints.size();
        int barSize = (cellCount+barCount-1)/barCount;
        
        int i = 0;
        
        for(Position p : datapoints) // increasing p.getX()
        {
            if(p.getX() < left)
                left = p.getX();
            if(p.getY() > upp)
                upp  = p.getY();
            
            bars[i].add(p);
            
            if(bars[i].size() == barSize)
                i++;
        }
    }
    
    public int getCellCount()
    {
        return this.cellCount;
    }
    
    public int getCount(int x, int y)
    {
        return this.getCount(x, y, 0);
    }
    
    public int getCount(int x, int y, int exactness)
    {
        if(x <= left && y >= upp) // all points included
            return getCellCount();
        else if(exactness == 0)
            return getEstimatedCount(x, y);
        else if(exactness == 1)
            return getAlmostExactCount(x, y);
        else
            return getExactCount(x, y);
    }
    
    private int getEstimatedCount(int x, int y)
    {
        int count = 0;
        
        for(int i=0; i<bars.length; i++)
        {
            if(bars[i].maxX >= x && bars[i].minY <= y)
                count += barCount;
        }
        
        return count;
    }
    
    private int getAlmostExactCount(int x, int y)
    {
        int count = 0;
        
        for(int i=0; i<bars.length; i++)
        {
            if(bars[i].minX >= x && bars[i].maxY <= y)
                count += barCount; // could be off by one when the datapoints could not be distributed evenly across all bars
            else if(bars[i].maxX >= x && bars[i].minY <= y)
                count += bars[i].getExactCount(x, y);
        }
        
        return count;
    }
    
    private int getExactCount(int x, int y)
    {
        int count = 0;
        
        for(int i=0; i<bars.length; i++)
        {
            if(bars[i].maxX >= x && bars[i].minY <= y)
                count += bars[i].getExactCount(x, y);
        }
        
        return count;
    }
    
    private class Bar extends PositionList<Position>
    {
        public int minX,maxX, minY,maxY;
        
        public Bar()
        {
            this.minX = Integer.MAX_VALUE;
            this.maxX = Integer.MIN_VALUE;
            this.minY = Integer.MAX_VALUE;
            this.maxY = Integer.MIN_VALUE;
        }
        /*
        public Bar(int minX, int maxX, int minY, int maxY)
        {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
        //*/
        
        @Override
        public boolean add(Position p)
        {
            if(p.getX() < minX)
                minX = p.getX();
            if(p.getX() > maxX)
                maxX = p.getX();
            if(p.getY() < minY)
                minY = p.getY();
            if(p.getY() > maxY)
                maxY = p.getY();
            
            return super.add(p);
        }
    }
}
