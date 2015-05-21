/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1p;

import labeler.Node;

/**
 *
 * @author francois
 */
public class Position
{
	private Node node;
    private int x,y;
    
    public Position(Node node, int x, int y)
    {
        this.node = node;
        this.x = x;
        this.y = y;
    }
    
    public Node getNode()
    {
        return this.node;
    }
    
    public int getX()
    {
        return this.x;
    }
    
    public int getY()
    {
        return this.y;
    }
}
