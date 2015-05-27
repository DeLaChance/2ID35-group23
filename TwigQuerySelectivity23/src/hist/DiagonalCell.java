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
public class DiagonalCell extends Cell
{
    public DiagonalCell(int barCount)
    {
        super(barCount);
    }
    
    @Override
    public int getCount(PositionList<QueryPoint> qps)
    {
        return super.getCount(qps, 1);
    }
}
