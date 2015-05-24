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
    
    public void generateHistogram()
    {
        super.generateHistogram(this.datapoints);
        this.histogramValid = true;
    }
    
    /**
     * Estimates the number of datapoints p with p.x >= x and p.y <= y.
     * 
     * @param x upper bound for x
     * @param y lower bound for y
     * @return estimated number of datapoints to the lower right of (x,y)
     */
    public int getCount(int x, int y)
    {
        if(!this.histogramValid)
            this.generateHistogram();
        
        return super.getCount(x, y);
    }
    
    /**
     * Calculates the number of datapoints p with p.x >= x and p.y <= y.
     * 
     * @param x upper bound for x
     * @param y lower bound for y
     * @return exact number of datapoints to the lower right of (x,y)
     */
    public int getExactCount(int x, int y)
    {
        int c = 0;
        for(Position p : this.datapoints)
        {
            if(p.getX() >= x && p.getY() <= y)
                c++;
        }
        
        return c;
    }
}