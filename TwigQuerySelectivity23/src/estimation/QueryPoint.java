/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estimation;

import hist.Position;

/**
 *
 * @author rik
 */
public class QueryPoint extends Position
{
    public QueryPoint(int x, int y)
    {
        super(x, y);
    }
    
    /**
     * Returns whether the current query point is a child of potentialParent
     * @param potentialParent
     * @return 
     */
    public boolean isChild(QueryPoint potentialParent) {
        return this.getX() >= potentialParent.getX() &&
                this.getY() >= potentialParent.getY();
    }
}
