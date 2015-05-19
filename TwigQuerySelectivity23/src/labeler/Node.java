/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labeler;

import java.util.HashMap;

/**
 *
 * @author lucien
 */
public class Node {
    
    private HashMap<String,String> attributes;
    private int id;
    
    public Node(HashMap<String,String> attributes, int id) 
    {
        this.attributes = attributes;
        this.id = id;
    }
    
    public void setAttributes(HashMap<String,String> attributes) 
    {
        this.attributes = attributes;
    }
    
    public HashMap<String,String> getAttributes()
    {
        return this.attributes;
    }
    
    public int getId()
    {
        return this.id;
    }
}
