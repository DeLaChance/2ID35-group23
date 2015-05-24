package c1p;

import java.util.Comparator;

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
		Row firstRow = inputMatrix.getRow(0);
		
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
			}
		}
		
		//Sort rows on overlap
		{
			inputMatrix.sort(new OverlapComparator());
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
		
		return subMatrix.append(heuristic(inputMatrix.remove(subMatrix)));
	}
	
	/**
	 * Check if a matrix has C1P.
	 */
	private boolean isC1P(Matrix m) {
		for(Row r : m)
		{
			//0: no ones detected yet
			//1: start of one streak detected
			//2: end of one streak detected
			int a = 0;
			
			for(int i = 0; i < r.size(); i++)
			{
				int v = r.get(i);
				
				if(a == 0 && v > 0)
					a++;
				else if(a == 1 && v == 0)
					a++;
				else if(a == 2 && v > 0)
					return false;
			}
		}
		
		return true;
	}
	/**
	 * Check if a row overlaps with a matrix.
	 */
	private boolean hasOverlap(Row r, Matrix m) {
		for(int i = 0; i < m.size(); i++)
		{
			Row row = m.getRow(i);

			//For each column
			for(int j = 0; j < row.size(); i++)
			{				
				boolean v1 = (r.get(j) != 0);
				boolean v2 = (row.get(j) != 0);

				if(v1 && v2)
					return true;
			}
		}
		
		return false;
	}
	/**
	 * Check if row is contained in matrix.
	 */
	private boolean containsRow(Matrix m, Row r) {
		for(int i = 0; i < m.size(); i++)
		{
			Row row = m.getRow(i);
			if(r.equals(row))
				return true;
		}
		
		return false;
	}
	
	private Matrix columnPartition(Matrix subMatrix, Row r) {
		return null;
	}
}
