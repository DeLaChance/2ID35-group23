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
public class Cell extends Equidepth
{
    private double rho;
    private PositionList<Position> datapoints = new PositionList<>();
    private boolean histogramValid = true;
    
    public Cell(int rho)
    {
        super(rho);
    }
    
    public void add(Position p)
    {
        this.histogramValid = false;
        this.datapoints.add(p);
    }
    
    public void rebuildHistogram()
    {
        super.rebuild(this.datapoints);
        this.histogramValid = true;
    }
    
    /**
     * Estimates the number of datapoints p with p.x >= x and p.y <= y.
     * 
     * @param x upper bound for x
     * @param y lower bound for y
     * @return estimated number of datapoints to the lower right of (x,y)
     */
    public int getCount(PositionList<QueryPoint> qps)
    {
        if(!this.histogramValid)
            this.rebuildHistogram();
        
        return super.getCount(qps);
    }
    
    /**
     * Calculates the number of datapoints p with p.x >= x and p.y <= y.
     * 
     * @param x upper bound for x
     * @param y lower bound for y
     * @return exact number of datapoints to the lower right of (x,y)
     */
    public int getExactCount(PositionList<QueryPoint> qps)
    {
        if(!this.histogramValid)
            this.rebuildHistogram();
        
        return super.getCount(qps, 2);
    }
    
    public PositionList<Position> getDatapoints()
    {
        return this.datapoints;
    }
    
    @Override
    public String toString()
    {
        String s = this.getCellCount() + " datapoints\n";
        
        for(Bar b : this.getBars())
        {
            s += "bar ("+b.count+"): x=["+b.minX+","+b.maxX+"], y=["+b.minY+","+b.maxY+"]\n";
        }
        
        return s;
    }
}
