/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist;

import java.util.TreeSet;
import c1p.Position;
import java.util.Comparator;

/**
 *
 * @author huib
 */
public class PositionList<P extends Position> extends TreeSet<P>
{
    private int maxY;
    
    public PositionList()
    {
        super(new Comparator<P>()
            {

                @Override
                public int compare(P o1, P o2)
                {
                    return o1.getX() - o2.getX();
                }
            });
    }
    
    public int getMaxY()
    {
        return this.maxY;
    }
    
    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Removal of datapoints is not implemented");
    }
    
    @Override
    public boolean add(P o)
    {
        if(o.getY() > this.maxY)
            this.maxY = o.getY();
        
        return super.add(o);
    }
}
