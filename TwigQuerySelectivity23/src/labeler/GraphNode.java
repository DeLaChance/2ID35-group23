package labeler;

import java.util.HashMap;

/**
* TODO: Description
*/
public class GraphNode 
{
    
    private String tag;
    private HashMap<String, String> attributes;
    private int id;
    
    public GraphNode(String tag, int id, HashMap<String, String> attributes)
    {
        this.tag = tag;
        this.id = id;
        this.attributes = attributes;
    }    
    
    public GraphNode(String tag, int id)
    {
        this.tag = tag;
        this.id = id;
        this.attributes = new HashMap<String, String>();
    }
    
    public void setTag(String tag) 
    {
        this.tag = tag;
    }
    
    public String getTag()
    {
        return this.tag;
    }

    public String getAttributeByName(String name)
    {
        if( this.attributes.containsKey(name) )
        {
            return this.attributes.get(name);
        }
        
        return null;
    }
    
    public void addAttribute(String name, String value)
    {
        if( this.attributes.containsKey(name) )
        {
            this.attributes.remove(name);
        }

        this.attributes.put(name, value);
    }
    
    public int getId()
    {
        return this.id;
    }
    
    public String getCustomId() 
    {
        if( this.attributes.containsKey("id") )
        {
            return this.attributes.get("id");
        }
        
        return null;
    }
}
