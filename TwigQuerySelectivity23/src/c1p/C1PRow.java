package c1p;

import hist.Position;
import labeler.Node;

/**
 *
 * @author francois
 */
public class C1PRow extends Position
{
	private Node node;
    
    public C1PRow(Node node, int fromColumnIndex, int toColumnIndex)
    {
        super(fromColumnIndex, toColumnIndex);
        this.node = node;
    }
    
    public Node getNode()
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
