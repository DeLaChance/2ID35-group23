package labeler;

import java.util.HashMap;

/**
* TODO: Description
*/
public class GraphNode {
    
    private String tag;
    private int id;
    private int depth;
    
    public GraphNode(String tag, int id, int depth) 
    {
        this.tag = tag;
        this.id = id;
        this.depth = depth;
    }
    
    public void setTag(String tag) 
    {
        this.tag = tag;
    }
    
    public String getTag()
    {
        return this.tag;
    }
    
    //Returns the Id of the node.
    public int getId()
    {
        return this.id;
    }
    
    //Returns the depth of the node.
    public int getDepth()
    {
        return this.depth;
    }
}
