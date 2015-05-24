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
	
	public c1p.Matrix heuristic(labeler.Matrix inputMatrix) {
		List<Row> matrix = new ArrayList<Row>();
		
		Row firstRow = (Row)inputMatrix.getRow(0);
		matrix.add(firstRow);
		
		//Calculate overlap
		{
			//For each row except the first
			for(int i = 1; i < inputMatrix.getMatrix().size(); i++)
			{
				Row row = (Row)inputMatrix.getRow(i);

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
		
		return null;
	}
}
