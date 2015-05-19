/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labeler;

/**
 *
 * @author lucien
 */
class Edge {
    private Pair<Integer,Integer> edge;
    
    public Edge(Pair<Integer,Integer> edge)
    {
        this.edge = edge;
    }
    
    public Integer getLeft()
    {
        return this.edge.getFirst();
    }
    
    public Integer getRight() 
    {
        return this.edge.getSecond();
    }    
    
    public Edge reverse()
    {
        Integer first = this.edge.getFirst();
        Integer second = this.edge.getSecond();
        
        Edge edge = new Edge(new Pair(second, first));
        
        return edge;
    }
}
