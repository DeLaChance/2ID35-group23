/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1p;

import java.util.ArrayList;
import java.util.List;
import labeler.GraphNode;

/**
 *
 * @author francois
 */
public class Row extends ArrayList<Integer> {
	private GraphNode node;
	private int overlap = 0;
		
	public GraphNode getNode() { return node; }
	public void setNode(GraphNode n) { this.node = node; }
	
	public int getOverlap() { return overlap; }
	public void setOverlap(int overlap) { this.overlap = overlap; }
	
	/**
	 * Get indexes of values that are non-zero.
	 * @return 
	 */
	public List<Integer> getNonZeroIndexes()
	{
		List<Integer> r = new ArrayList<>();
		for(int i = 0; i < this.size(); i++)
		{
			if(this.get(i) != 0)
				r.add(i);
		}
		return r;
	}
}