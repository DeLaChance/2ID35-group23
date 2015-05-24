package c1p;

import labeler.Node;

/**
 *
 * @author francois
 */
public class C1PRow
{
	private Node node;
    private int fromColumnIndex;
	private int toColumnIndex;
    
    public C1PRow(Node node, int fromColumnIndex, int toColumnIndex)
    {
        this.node = node;
        this.fromColumnIndex = fromColumnIndex;
        this.toColumnIndex = toColumnIndex;
    }
    
    public Node getNode()
    {
        return this.node;
    }
    
    public int getFromColumnIndex()
    {
        return this.fromColumnIndex;
    }
    
    public int getToColumnIndex()
    {
        return this.toColumnIndex;
    }
}
