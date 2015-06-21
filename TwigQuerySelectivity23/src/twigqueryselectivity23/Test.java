/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package twigqueryselectivity23;

import c1p.C1PMatrix;
import c1pGenerator.C1PMatrixGenerator;
import c1pGenerator.CustomBranchingDistribution;
import c1pGenerator.Distribution;
import estimation.QueryPoint;
import hist.Histogram;
import hist.Position;
import hist.PositionList;
import hist.debugView.MainView;

/**
 *
 * @author huib
 */
public class Test
{
    public static final void main(String[] args) throws Throwable
    {
		// Only run C1P matrix generator:
        //testMatrixGenerator();
        
        // Run test queries based on commandline parameters
        //*
        testHistogram(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                new CustomBranchingDistribution(),
                Double.parseDouble(args[2]),
                Double.parseDouble(args[3]),
                Integer.parseInt(args[4]),
                args.length > 5 && "--summary".equals(args[5])
            );
        //*/
        
        // Generate default C1P matrix and start gui
        //showHistogram();
    }
    
	public static void testMatrixGenerator()
	{
		C1PMatrix m = C1PMatrixGenerator.createMatrix();
		System.out.println(m.toString());
	}
	
    public static void showHistogram()
    {
        Histogram hist = new Histogram(C1PMatrixGenerator.createMatrix());
        
        MainView mv = new MainView(hist);
    }
    
    public static void testHistogram(int height, int leafs, Distribution d, double depthVar, double interconnectedness, int queryCount, boolean summaryOnly)
    {
        Histogram hist = new Histogram(C1PMatrixGenerator.createTree(height, leafs, d, depthVar, interconnectedness));
        
        double avgEst=0, avgDet=0, avgEstTime=0, avgDetTime=0, RMSE=0, NRMSE=0;
        boolean estFirst = true;
        
        int i = 0;
        double r = queryCount/(double)hist.getDatapoints().size();
        //for(int i=0; i<queryCount; i++)
        for(Position p : hist.getDatapoints())
        {
            if(r >= 1 || Math.random() > r)
                continue;
            
            try
            {
                //int[] q = C1PMatrixGenerator.randPosition(hist.getMaxY());
                
                QueryPoint qp = new QueryPoint(p.getX(), p.getY()); //new QueryPoint(q[0], q[1]);
                PositionList<QueryPoint> query = new PositionList<>();
                query.add(qp);

                long time1, time2, time3, time4;
                int est, det;
                if(estFirst)
                {
                    time1 = System.nanoTime();
                    est = hist.estimateCount(query);
                    time2 = System.nanoTime();
                    time3 = System.nanoTime();
                    det = hist.determineCount(query);
                    time4 = System.nanoTime();
                    
                    estFirst = false;
                }
                else
                {
                    time3 = System.nanoTime();
                    det = hist.determineCount(query);
                    time4 = System.nanoTime();
                    time1 = System.nanoTime();
                    est = hist.estimateCount(query);
                    time2 = System.nanoTime();
                    
                    estFirst = true;
                }
                
                if(!summaryOnly && query.size() == 1)
                {
                    int absX = query.first().getX();
                    int absY = query.first().getY();
                    int relX = absX%hist.getCellSize();
                    int relY = absY%hist.getCellSize();
                    if(relY == 0)
                        relY = 1;
                    
                    System.out.println(absX+","+absY+","+relX+","+relY+","+est+","+det+","+(est/(double)det)+","+((time2-time1)/1000f)+","+((time4-time3)/1000f));
                }

                avgEst += est;
                avgDet += det;
                RMSE += (est-det)*(est-det);
                avgEstTime += (time2-time1)/1000f;
                avgDetTime += (time4-time3)/1000f;
                i++;
            }
            catch(Exception ex)
            {
                System.err.println(ex.getMessage());
            }
        }
        
        if(queryCount > 0)
        {
            avgEst     /=queryCount;
            avgDet     /=queryCount;
            RMSE       /=queryCount;
            NRMSE       =RMSE/avgDet;
            avgEstTime /=queryCount;
            avgDetTime /=queryCount;
        }
        
        if(summaryOnly)
            System.out.println(
                    height+",\t"+leafs+",\t"+depthVar+",\t"+interconnectedness+",\t"+i+",\t"+avgEst+",\t"+avgDet+",\t"+avgEstTime+",\t"+avgDetTime+",\t"+RMSE+",\t"+NRMSE
            );
    }
}
