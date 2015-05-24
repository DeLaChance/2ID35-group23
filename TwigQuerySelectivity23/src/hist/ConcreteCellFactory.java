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
public class ConcreteCellFactory implements CellFactory<Cell>
{
    private int barCount;
    
    public ConcreteCellFactory(int barCount)
    {
        this.barCount = barCount;
    }
    
    @Override
    public Cell create(boolean isOnDiagonal)
    {
        if(isOnDiagonal)
            return new DiagonalCell(barCount);
        else
            return new Cell(barCount);
    }
}
