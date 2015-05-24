/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1p;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author francois
 */
public class C1P {
	private static class OverlapComparator implements Comparator<Row> {
		@Override
		public int compare(Row a, Row b) {
			return a.getOverlap() < b.getOverlap() ? -1 : a.getOverlap() == b.getOverlap() ? 0 : 1;
		}
	}
	
	public Matrix heuristic(Matrix inputMatrix) {
		Matrix matrix = new Matrix();
		
		Row firstRow = inputMatrix.getRow(0);
		matrix.add(firstRow);
		
		//Calculate overlap
		{
			//For each row except the first
			for(int i = 1; i < inputMatrix.size(); i++)
			{
				Row row = inputMatrix.getRow(i);

				//For each column
				for(int j = 0; j < row.size(); i++)
				{				
					boolean v1 = (firstRow.get(j) != 0);
					boolean v2 = (row.get(j) != 0);

					if(v1 && v2)
						row.setOverlap(row.getOverlap() + 1);
				}

				matrix.add(row);
			}
		}
		
		//Sort rows on overlap
		{
			matrix.sort(new OverlapComparator());
		}
		
		//Create submatrix
		Matrix subMatrix = new Matrix();
		{
			subMatrix.add(firstRow);
			for(int i = 1; i < inputMatrix.size(); i++)
			{
				Row currentRow = (Row)inputMatrix.getRow(i);
				
				Matrix joined = new Matrix();
				joined.addAll(subMatrix);
				joined.add(currentRow);

				if(isC1P(joined) ||
						hasOverlap(currentRow, subMatrix) ||
						containsRow(subMatrix, currentRow))
				{
					subMatrix = columnPartition(subMatrix, currentRow);
				}
			}
		}
		
		return Matrix.append(subMatrix, heuristic(Matrix.remove(inputMatrix, subMatrix)));
	}
	
	/**
	 * Check if a matrix has C1P.
	 */
	private boolean isC1P(Matrix m) {
		return false;
	}
	/**
	 * Check if a row overlaps with a matrix.
	 */
	private boolean hasOverlap(Row r, Matrix m) {
		return false;
	}
	/**
	 * Check if row is contained in matrix.
	 */
	private boolean containsRow(Matrix m, Row r) {
		return false;
	}
	
	private Matrix columnPartition(Matrix subMatrix, Row r) {
		return null;
	}
}
