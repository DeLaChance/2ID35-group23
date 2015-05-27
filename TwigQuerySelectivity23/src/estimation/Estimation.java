/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estimation;

import java.util.HashSet;
import java.util.Set;
import labeler.Graph;

/**
 *
 * @author rik
 */
public class Estimation {

    private int topDown(Query query, Set<QueryPoint> points, Graph g) {
        //Case 1: //A/p' => A is a tag
        if(query.containsNextQuery()) {
            Set<QueryPoint> nextPoints = estimateIntermediate(query.getStep(), points);
            return topDown(query.getNextQuery(), nextPoints, g);
        }
        else if(!query.containsFilter() && !query.containsNextQuery()) {
            return estimateCount(query.getStep(), points);
        }
        else if(query.containsFilter() && query.containsNextQuery()) {
            Set<QueryPoint> filteredPoints = bottomUp(query.getFilter(), null, g);
            Set<QueryPoint> nextPoints = estimateIntermediate(query.getStep(), points);
            for(QueryPoint point : nextPoints) {
                if(!atRightBottom(point, filteredPoints)) {
                    nextPoints.remove(point);
                }
            }
        }
        else if(query.containsFilter()) {
            Set<QueryPoint> filteredPoints = bottomUp(query.getFilter(), null, g);
            return estimateCountWithQf(query.getStep(), points, filteredPoints);
        }
        //Does never reach this statement.
        return 0;
    }

    private Set<QueryPoint> bottomUp(FilterQuery q, Set<QueryPoint> P, Graph g) {
        
    }

    private Set<QueryPoint> estimateIntermediate(String queryStep, Set<QueryPoint> points) {
        Set<QueryPoint> nextPoints = new HashSet<QueryPoint>();
        for (QueryPoint point : points) {
            //for each cell c bottom right of point
                //if c in case I, II, III
        }
        return nextPoints;
    }
    
    private Set<QueryPoint> estimateIntermediateReverse(String queryStep, Set<QueryPoint> points) {
        return null;
    }

    private int estimateCount(String step, Set<QueryPoint> points) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean atRightBottom(QueryPoint point, Set<QueryPoint> filteredPoints) {
        return true;
    }

    private int estimateCountWithQf(String step, Set<QueryPoint> points, Set<QueryPoint> filteredPoints) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
