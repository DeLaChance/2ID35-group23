/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist;

import estimation.QueryPoint;

/**
 *
 * @author huib
 */
public class Equidepth
{
    private int barCount;
    private int cellCount = 0;
    private int left=Integer.MAX_VALUE, upp=Integer.MIN_VALUE;
    private Bar[] bars; // ordered width ascending minX
    
    public Equidepth(int barCount)
    {
        this.barCount = barCount;
        this.bars =  new Bar[barCount];
        for(int i=0; i<barCount; i++)
            this.bars[i] = new Bar();
    }
    
    public int getBarCount()
    {
        return this.barCount;
    }
    
    public Bar[] getBars()
    {
        return this.bars;
    }
    
    protected void rebuild(PositionList<? extends Position> datapoints)
    {
        cellCount = datapoints.size();
        int unAssignedPoints = cellCount;
        
        int i = 0;
        
        for(Position p : datapoints) // increasing p.getX()
        {
            int barSize = (unAssignedPoints+barCount-i-1)/(barCount-i);
            
            if(p.getX() < left)
                left = p.getX();
            if(p.getY() > upp)
                upp  = p.getY();
            
            bars[i].add(p);
            
            if(bars[i].size() == barSize)
            {
                i++;
                unAssignedPoints -= barSize;
            }
        }
    }
    
    public int getCellCount()
    {
        return this.cellCount;
    }
    
    public int getCount(PositionList<QueryPoint> qps)
    {
        return this.getCount(qps, 0);
    }
    
    public int getCount(PositionList<QueryPoint> qps, int exactness)
    {
        boolean allPointsIncluded = false;
        
        for(QueryPoint qp : qps)
        {
            allPointsIncluded = qp.getX() <= left && qp.getY() >= upp;
            if(allPointsIncluded)
                break;
        }
        
        if(allPointsIncluded)
            return getCellCount();
        else if(exactness == 0)
            return getEstimatedCount(qps);
        else if(exactness == 1)
            return getAlmostExactCount(qps);
        else
            return getExactCount(qps);
    }
    
    private int getEstimatedCount(PositionList<QueryPoint> qps)
    {
        int count = 0;
        
        for(int i=0; i<bars.length; i++)
        {
            if(bars[i].count != 0) //skip empty bars;
            {
                //boolean included = false;
                QueryPoint p = null;

                for(QueryPoint qp : qps)
                {
                    if(bars[i].maxX >= qp.getX() && bars[i].minY <= qp.getY())
                    {
                        p = qp;
                        break;
                    }
                }

                if(p != null)
                    count += 0.5+ //rounding
                            Math.min(1,(bars[i].maxX-p.getX())/(double)(bars[i].maxX-bars[i].minX))*
                            Math.min(1,(p.getY()-bars[i].minY)/(double)(bars[i].maxY-bars[i].minY))*
                            bars[i].getTotalCount();
            }
        }
        
        return count;
    }
    
    private int getAlmostExactCount(PositionList<QueryPoint> qps)
    {
        int count = 0;
        
        for(int i=0; i<bars.length; i++)
        {
            boolean allIncluded = false;
            
            for(QueryPoint qp : qps)
            {
                allIncluded = bars[i].minX >= qp.getX() && bars[i].maxY <= qp.getY();
                if(allIncluded)
                    break;
            }
            
            if(allIncluded)
                count += bars[i].getTotalCount();
            else
            {
                boolean included = false;

                for(QueryPoint qp : qps)
                {
                    included = bars[i].maxX >= qp.getX() && bars[i].minY <= qp.getY();
                    if(included)
                        break;
                }
                if(included)
                    count += bars[i].getExactCount(qps);
            }
        }
        
        return count;
    }
    
    private int getExactCount(PositionList<QueryPoint> qps)
    {
        int count = 0;
        
        for(int i=0; i<bars.length; i++)
        {
            boolean included = false;

            for(QueryPoint qp : qps)
            {
                included = bars[i].maxX >= qp.getX() && bars[i].minY <= qp.getY();
                if(included)
                    break;
            }
            
            if(included)
                count += bars[i].getExactCount(qps);
        }
        
        return count;
    }
    
    public class Bar extends PositionList<Position>
    {
        public int minX,maxX, minY,maxY, count=0;
        
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
            
            count++;
            
            return super.add(p);
        }
        
        public int getTotalCount()
        {
            return this.count;
        }
    }
}
