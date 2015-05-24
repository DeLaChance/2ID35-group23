/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist;

/**
 *
 * @author huib
 * 
 * Cells positioned as below (axis show maxY and minX, cellwidth
 * assumed to be 2 for this example):
 * 12 [p][q][r][s][t][u]
 * 10 [k][l][m][n][o]
 *  8 [g][h][i][j]
 *  6 [D][E][F]
 *  4 [B][C]
 *  2 [A]
 *     0  2  4  6  8  10
 * 
 * Stored in a 2D array[7][3] like:
 *    0  1  2  3  4  5  6
 * 0 [A][u][t][s][r][q][p]
 * 1 [B][C][o][n][m][l][k]
 * 2 [D][E][F][j][i][h][g]
 * 
 * 
 * Or with and odd size:
 * 10 [k][l][m][n][o]
 *  8 [g][h][i][j]
 *  6 [d][e][f]
 *  4 [B][C]
 *  2 [A]
 *     0  2  4  6  8 
 * 
 * Stored in a 2D array[5][3] like:
 *    0  1  2  3  4 
 * 0 [o][n][m][l][k]
 * 1 [A][j][i][h][g]
 * 2 [B][C][d][e][f]
 * 
 */
public class TriangleGrid<T>
{
    protected Ref[][] grid;
    private int maxY;
    private int width; // number of cells in x (or y) direction, width=height
    private int cellSize;
    
    public TriangleGrid(int maxWidth, int maxY)
    {
        this.maxY = maxY;
        this.cellSize = (maxY+maxWidth-1)/maxWidth;
        
        
        this.width = (maxY+cellSize-1)/cellSize;
        this.grid = new TriangleGrid.Ref[width+1-(width&1)][(width+1)/2];
    }
    
    public final void populate(CellFactory<T> factory)
    {
        int i =0;
        for(int h=0; h<grid.length; h++)
            for(int v=0; v<grid[h].length; v++)
                grid[h][v] = new Ref(factory.create(isOnDiagonal(h, v)));
    }
    
    public T getCell(int x, int y)
    {
        checkInGrid(x,y);
        int[] p = convert(x,y);
        try {
            return (T)this.grid[p[0]][p[1]].getV();
        } catch(ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("Position ("+x+","+y+")->("+p[0]+","+p[1]+") not in grid!?", e);
        }
    }
    
    protected int[] convert(int x, int y)
    {
        // l= number of cells positioned left of the cell containing x.
        int l = x/cellSize;
        // d= number of cells below of the cell containing y.
        int d = y/cellSize;
        
        
        if(d < width/2)
            return new int[] {l, d + (width&1)};
        else
            return new int[] {width - l - (width&1), width - d - 1};
    }
    
    // returns true iff this.grid[h][v] is a cell positioned (x,y) on the diagonal
    protected boolean isOnDiagonal(int h, int v)
    {
        return h==v || h+(width&1)==v+1-(width&1);
    }

    private void checkInGrid(int x, int y)
    {
        if(!isInGrid(x,y))
        {
            int[] p = convert(x,y);
            throw new IllegalArgumentException("position ("+x+","+y+")->("+p[0]+","+p[1]+") not in grid");
        }
    }
    private boolean isInGrid(int x, int y)
    {
        return x<y && y < maxY;
    }
    
    private class Ref
    {
        //Ugly objects to avoid "generic array creation"
        private Object v;
        public Ref(Object v)
        {
            this.v = v;
        }
        
        public Object getV()
        {
            return this.v;
        }
    }
    
    @Override
    public String toString()
    {
        //return "width: "+width+"\ncellSize: "+cellSize+"\nmax x/y: "+maxY+"\n";
        
        //*
        String s = "";
        
        s+= "width: "+width+"\ncellSize: "+cellSize+"\nmax x/y: "+maxY+"\n";
        s+= "array["+grid.length+"]["+grid[0].length+"]\n";
        s+= "Triangle:\n";
        
        for(int y=maxY-1; y>=0; y--)
        {
            for(int x=0; x<maxY; x++)
                if(isInGrid(x,y))
                {
                    //int[] p = convert(x,y);
                    //s += String.format(" %2d,%2d ", p[0],p[1]);
                    s += " "+getCell(x,y);
                }
            s += "\n";
        }
        
        return s;
        //*/
    }
}
