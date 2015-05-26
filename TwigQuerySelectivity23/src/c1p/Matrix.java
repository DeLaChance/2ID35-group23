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
	
	public static Matrix append(Matrix m1, Matrix m2) {
		return m1;
	}
	
	public static Matrix remove(Matrix m1, Matrix m2) {
		return m1;
	}
}
