/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist;

import estimation.QueryPoint;
import java.util.TreeSet;
import java.util.Comparator;

/**
 *
 * @author huib
 */
public class PositionList<P extends Position> extends TreeSet<P>
{
	private int maxX;
    private int maxY;
    
    public PositionList()
    {
        super(new Comparator<P>()
            {

                @Override
                public int compare(P o1, P o2)
                {
                    int c;
                    
                    if(o1.getX() != o2.getX())
                        c = o1.getX() - o2.getX();
                    else if(o1.getY() != o2.getY())
                        c = o2.getY() - o1.getY();
                    else
                        c = o1.getID() - o2.getID();
                    
                    return c;
                }
            });
    }
    
	public int getMaxX()
	{
		return this.maxX;
	}
	
    public int getMaxY()
    {
        return this.maxY;
    }
    
    /**
     * Returns number of datapoints p with p.x>=x and p.y<=y, use only for small scale!.
     * 
     * @param x
     * @param y
     * @return 
     */
    public int getExactCount(PositionList<QueryPoint> qps)
    {
        int c = 0;
        for(Position p : this)
        {
            boolean included = false;

            for(QueryPoint qp : qps)
            {
                included = p.getX() >= qp.getX() && p.getY() <= qp.getY();
                if(included)
                    break;
            }
            
            if(included)
                c++;
        }
        
        return c;
    }
    
    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Removal of datapoints is not implemented");
    }
    
    @Override
    public boolean add(P o)
    {
		if(o.getX() > this.maxX)
			this.maxX = o.getX();
        if(o.getY() > this.maxY)
            this.maxY = o.getY();
        
        return super.add(o);
    }
}
