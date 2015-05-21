/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1p;

import java.util.ArrayList;

/**
 *
 * @author francois
 */
public class Row extends ArrayList<Integer> {
	private int overlap = 0;
	
	public int getOverlap() { return overlap; }
	public void setOverlap(int overlap) { this.overlap = overlap; }
}