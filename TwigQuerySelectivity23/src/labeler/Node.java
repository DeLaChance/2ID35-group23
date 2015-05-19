package labeler;

import java.util.HashMap;

/**
* TODO: Description
*/
public class Node {
    
    private HashMap<String,String> attributes;
    private int id;
    
    //Constructor of the Node which sets the attributes of the node and its Id.
    public Node(HashMap<String,String> attributes, int id) 
    {
        this.attributes = attributes;
        this.id = id;
    }
    
    //Set the attributes of the node.
    public void setAttributes(HashMap<String,String> attributes) 
    {
        this.attributes = attributes;
    }
    
    //Returns the attributes of a given node.
    public HashMap<String,String> getAttributes()
    {
        return this.attributes;
    }
    
    //Returns the Id of the node.
    public int getId()
    {
        return this.id;
    }
}
