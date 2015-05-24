/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist;

import c1p.Position;
import twigqueryselectivity23.TwigQuerySelectivity23;

/**
 *
 * @author huib
 */
public class Histogram extends TriangleGrid<Cell>
{
    public Histogram(PositionList<Position> datapoints)
    {
        super(TwigQuerySelectivity23.HIST_GRID_WIDTH, datapoints.getMaxY());
        
        this.populate(new ConcreteCellFactory(TwigQuerySelectivity23.HIST_CELL_BARS));
        this.populateCells(datapoints);
    }
    
    private void populateCells(PositionList<Position> datapoints)
    {
        for(Position p : datapoints)
        {
            this.getCell(p.getX(), p.getY()).add(p);
        }
    }
}
