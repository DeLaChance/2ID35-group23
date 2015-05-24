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
	
	public Matrix append(Matrix m) {
		//Resize the rows to contain the correct number of columns
		{
			int c1 = this.get(0).size();
			int c2 = m.get(0).size();
		
			//Append 0s to end of each row of first matrix
			for(int i = 0; i < c2; i++)
			{
				for(Row r : this)
				{
					r.add(0);
				}
			}

			//Prepend 0s at the start of each row of the second matrix
			for(int i = 0; i < c1; i++)
			{
				for(Row r : m)
				{
					r.add(0, 0);
				}
			}
		}
		
		//Add rows so matrix has correct number of rows
		this.addAll(m);
		
		return this;
	}
	
	public Matrix remove(Matrix m) {
		return m;
	}
}
