package labeler;

import java.util.HashMap;

/**
* TODO: Description
*/
public class GraphNode {
    
    private String tag;
    private int id;
    
    public GraphNode(String tag, int id) 
    {
        this.tag = tag;
        this.id = id;
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
}
