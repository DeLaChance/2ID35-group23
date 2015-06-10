/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package c1pGenerator;

import c1p.C1PMatrix;
import c1p.C1PRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author huib
 */
public class C1PMatrixGenerator
{
    public static C1PMatrix createMatrix()
    {
        C1PMatrix m = new C1PMatrix();
        int max = twigqueryselectivity23.TwigQuerySelectivity23.GEN_COLUMN_COUNT;
        
        for(int i=0; i<100000; i++)
        {
            int x,y;
            do
            {
                x = randInt(0,2*max);
                int d = randInt2(0,(max+1));
                y = (x+d)/2;
                x = (x-d)/2;
            }
            while(!(x<y && x>=0 && y>=0 && y<max));
            
            m.add(new C1PRow(null, x, y));
        }
        
        return m;
    }
    
	/*
	public static C1PMatrix createMatrix3()
	{
		//Create a random number of trees
		C1PMatrix m = new C1PMatrix();
		
		int offset = 0;
		for(int i = 0; i < 100; i++)
		{
			List<C1PRow> rows = createTree(5000, randInt(0, 5000), randInt(0, 5000));
			
			int maxX = offset;
			for(C1PRow row : rows)
			{
				C1PRow newRow = new C1PRow(null, row.getX() + offset, row.getY() + offset);
				m.add(newRow);
				
				if(newRow.getY() > maxX)
					maxX = newRow.getY();
			}
			
			offset = maxX;
		}
		
		return m;
	}
	public static List<C1PRow> createTree(int columns, int leafs, int treeHeight)
	{
		List<C1PRow> r = new ArrayList<>();
		
		if(treeHeight < 1)
			return r;
		
		List<C1PRow> newRows = new ArrayList<>();
		newRows.add(createRow(columns - leafs, leafs)); //add leafs
		for(int level = treeHeight - 2; level >= 0; level--)
		{
			//Build a tree around the leafs
			C1PRow previousRow = newRows.get(newRows.size() - 1);
			int columnsLeft = columns - (previousRow.getY() - previousRow.getX());
			if(columnsLeft == 0)
				break;

			C1PRow newRow = null;
			
			int levelsLeft = level; //for clarity
			if(columnsLeft == levelsLeft) {
				//Add one 1 per row
				if(previousRow.getY() == columns - 1)
					newRow = previousRow.prependOne();
				else if(previousRow.getX() == 0)
					newRow = previousRow.appendOne();
				else
					newRow = randBool() ? previousRow.prependOne() : previousRow.appendOne();
			}
			else if(columnsLeft < levelsLeft) { 
				//Add  zero or one 1 per row
				boolean add = randBool();
				
				if(add) { //add a new row with a 1 prepended or appended
					if(previousRow.getY() == columns - 1)
						newRow = previousRow.prependOne();
					else if(previousRow.getX() == 0)
						newRow = previousRow.appendOne();
					else
						newRow = randBool() ? previousRow.prependOne() : previousRow.appendOne();
				}
				else //simply copy the previous row
					newRow = new C1PRow(previousRow);
			} 
			else if(columnsLeft > levelsLeft) {
				//Add more than one 1 per row
				int maxOnesToAdd = columnsLeft - levelsLeft;
				int onesToAdd = randInt(1, maxOnesToAdd);
				
				C1PRow c = new C1PRow(previousRow);
				for(int i = 0; i < onesToAdd; i++)
				{
					if(c.getY() == columns - 1)
						c = c.prependOne();
					else if(c.getX() == 0)
						c = c.appendOne();
					else
						c = randBool() ? c.prependOne() : c.appendOne();
				}
				newRow = c;
			}
			
			newRows.add(newRow);
		}
		
		while(!newRows.isEmpty())
		{
			r.add(newRows.remove(newRows.size() - 1));
		}
		
		return r;
	}
	*/
	
	public static C1PMatrix createMatrix2()
	{
		return createTree(100, 10, 2);
	}
	
	private static C1PMatrix createTree(int maxColumns, int leafs, int branchingFactor)
	{
		C1PMatrix r = new C1PMatrix();
		
		C1PMatrix l = createLeafs(leafs);
		r.addAll(l);
		
//		//Create parents for the leafs
//		{
//			int parentX = Integer.MAX_VALUE;
//			int parentY = Integer.MIN_VALUE;
//			for(C1PRow leafRow : l)
//			{
//				if(leafRow.getX() <= parentX)
//					parentX = leafRow.getX();
//				if(leafRow.getY() >= parentY)
//					parentY = leafRow.getY();
//
//				if(parentY - parentX == branchingFactor)
//				{
//					r.add(new C1PRow(null, parentX, parentY));
//					parentX = Integer.MAX_VALUE;
//					parentY = Integer.MIN_VALUE;
//				}
//			}
//			if(parentX != Integer.MAX_VALUE && parentY != Integer.MIN_VALUE) //parent with less children than branching factor
//				r.add(new C1PRow(null, parentX, parentY));
//		}
		
		//Keep creating parents until we have a large tree
		int nextX = 0;
		while(r.getMaxY() < maxColumns && r.getMaxY() > nextX)
		{
			int parentX = Integer.MAX_VALUE;
			int parentY = Integer.MIN_VALUE;
			
			for(C1PRow row : r)
			{
				if(row.getX() < nextX) //fast forward to the next leaf
					continue;
				
				//Should we take the current row for the parent?
				boolean take = randBool();
				if(!take)
					break;

				//Make the row a child of the parent
				if(row.getX() < parentX)
					parentX = row.getX();
				if(row.getY() > parentY)
					parentY = row.getY() + 1;
				
				nextX = row.getY() + 1;
			}
			
			if(parentX == Integer.MAX_VALUE || parentY == Integer.MIN_VALUE)
				continue;
			
			r.add(new C1PRow(null, parentX, parentY));
		}
				
		return r;
	}
	private static C1PMatrix createLeafs(int count)
	{
		C1PMatrix r = new C1PMatrix();
		
		for(int i = 0; i < count; i++)
		{
			r.add(new C1PRow(null, i, i+1));
		}
		
		return r;
	}
	
	private static C1PRow createRow(int maxX, int width)
	{
		int x = randInt(0, maxX);
		int y = x + width;
		return new C1PRow(null, x, y);
	}
	
    // random int in range [min, max) 
    // i.e. including min, excluding max
    private static int randInt(int min, int max)
    {
        assert min < max;
        
        return min+(int)((max-min)*Math.random());
    }
    
    private static int randInt2(int min, int max)
    {
        assert min < max;
        
        double r = Math.random();
        return min+(int)((max-min)*r*r*r);
    }
	
	private static boolean randBool()
	{
		return new Random().nextBoolean();
	}
}
