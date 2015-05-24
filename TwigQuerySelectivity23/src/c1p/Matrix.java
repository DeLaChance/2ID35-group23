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
public class Matrix extends ArrayList<Row> {
	public Row getRow(int i) {
		return get(i);
	}
	
	public Matrix append(Matrix m1, Matrix m2) {
		return m1;
	}
}
