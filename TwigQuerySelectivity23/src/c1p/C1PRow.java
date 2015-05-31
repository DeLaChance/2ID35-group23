package c1p;

import hist.Position;
import labeler.GraphNode;

/**
 *
 * @author francois
 */
public class C1PRow extends Position
{
	private GraphNode node;
    
    public C1PRow(GraphNode node, int fromColumnIndex, int toColumnIndex)
    {
        super(fromColumnIndex, toColumnIndex);
        this.node = node;
    }
    
    public GraphNode getNode()
    {
        return this.node;
    }
    
    public int getFromColumnIndex()
    {
        return this.getX();
    }
    
    public int getToColumnIndex()
    {
        return this.getY();
    }
}
