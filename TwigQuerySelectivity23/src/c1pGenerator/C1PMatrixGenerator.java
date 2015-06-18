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
    public static C1PMatrix createMatrix2()
    {
        C1PMatrix m = new C1PMatrix();
        int max = twigqueryselectivity23.TwigQuerySelectivity23.GEN_COLUMN_COUNT;
        
        for(int i=0; i<100000; i++)
        {
            int[] p = randPosition(max);
            
            m.add(new C1PRow(null, p[0], p[1]));
        }
        
        return m;
    }
    
    public static int[] randPosition(int max)
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
        
        return new int[] {x, y};
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
	
	public static C1PMatrix createMatrix()
	{
		int leafs = 300000;
        int treeHeight = 100;
        Distribution branchingDist = new CustomBranchingDistribution();
        double leafDepthVar = 0.3;
        double interconnectedness = 0.3;
        
		System.out.println("leafs: " + leafs);
		return createTree(treeHeight, leafs, branchingDist, leafDepthVar, interconnectedness);
	}
	
    /**
     * 
     * @param treeHeight
     * @param leafs
     * @param branchingChance
     * @param leafDepthVar in (0,1], 1 means all leafs are at the same height (maximal), 0.5 means
     * about half the leafs are at maximal depth, 0.25 at maximal-1, 0.125 at max-2, etc.
     * @return 
     */
	private static C1PMatrix createTree(int treeHeight, int leafs, Distribution branchingDist, double leafDepthVar, double interconnectedness)
	{
		C1PMatrix r = new C1PMatrix();
		
		//First add a set of leafs
		C1PMatrix l = createLeafs(leafs);
		r.addAll(l);
        treeHeight--; //added leafs, one level completed
		
		//Keep creating parents until we have a large tree
		int nextX = 0;
		while(treeHeight > 0)
		{
			//System.out.println("Add new layer to graph, depth="+treeHeight);
            int parentX = Integer.MAX_VALUE;
			int parentY = Integer.MIN_VALUE;
            int numChilds = 0;
            boolean adopting = true;
            C1PMatrix newParents = new C1PMatrix();
			
			for(C1PRow row : r)
			{
                boolean readyForNext = true;
                do
                {
                    // unless changed in de code below, we'll be ready for the next potential child
                    // at the end of this loop
                    readyForNext = true;
                    
                    //System.out.print("("+parentX+"-"+parentY+") "+row.getX() + ", "+nextX+": ");

                    if(row.getX() < nextX) //fast forward to the next potential child
                    {
                        //System.out.println("already in family, skip");
                        continue;
                    }

                    if(parentX == Integer.MAX_VALUE || parentY == Integer.MIN_VALUE) // if current parent has no childs yet
                    {
                        if(randDouble() > leafDepthVar) // leave some orphans to be adopted by parents higher up the tree
                        {
                            nextX++;
                            //System.out.println("leave orphans, skip");
                            continue;
                        }
                    }

                    if(adopting && randDouble() > branchingDist.chanceForMore(numChilds)) //should we stop adopting?
                    {
                        adopting = false;
                        //System.out.println(numChilds+" children already, let's stop");
                    }

                    if(adopting) // should we adopt the next child in line?
                    {
                        if(row.getX() == nextX)
                        {
                            //Make the row a child of the parent
                            if(row.getX() < parentX)
                                parentX = row.getX();

                            parentY = row.getY();

                            if(randDouble() > interconnectedness) // adopt definitively
                            {
                                nextX = parentY;
                                numChilds++;
                                //System.out.println("adopt definitively");
                                //System.out.println("ADOPT: "+row.getX()+"-"+row.getY());
                            }
                            //else
                                //System.out.println("adopt temptorarily, hoping a better candidate comes along");
                        }
                        else
                        {
                            nextX = parentY;
                            numChilds++;
                            readyForNext = false; // now we've decided about the previous, revisit the current
                            //System.out.println("no better candidate, adopting last candidate definitively");
                        }
                    }
                    else
                    {
                        //System.out.println("NEW NODE: "+parentX+"-"+parentY+" (depth="+treeHeight+" | childs "+numChilds+")");

                        newParents.add(new C1PRow(null, parentX, parentY));
                        nextX = parentY;
                        parentX = Integer.MAX_VALUE;
                        parentY = Integer.MIN_VALUE;
                        numChilds = 0;
                        adopting = true;
                        
                        //revisit the current child
                        readyForNext = false;
                    }
                }
                while(!readyForNext);
			}
            
            r.addAll(newParents);
            newParents.clear();
			nextX = 0;
			treeHeight--;
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
	
	private static Random random = new Random();
	private static boolean randBool()
	{
		return random.nextBoolean();
	}
	
	private static double randDouble()
	{
		return random.nextDouble();
	}
}
