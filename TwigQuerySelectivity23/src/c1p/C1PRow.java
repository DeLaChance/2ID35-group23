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
    
	public C1PRow(C1PRow other) {
		super(other.getX(), other.getY());
	}
	
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
	
	public C1PRow prependOne()
	{
		return new C1PRow(null, this.getX() - 1, this.getY());
	}
	public C1PRow appendOne()
	{
		return new C1PRow(null, this.getX(), this.getY() + 1);
	}
	
	public boolean isLeaf()
	{
		return Math.abs(this.getY() - this.getX()) == 1;
	}
}
