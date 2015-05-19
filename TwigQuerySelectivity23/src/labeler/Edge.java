package labeler;

/**
 * TODO: Documentation
 */
class Edge {
    private Pair<Integer,Integer> edge;
    
    //Constructor which initialize the edge.
    public Edge(Pair<Integer,Integer> edge)
    {
        this.edge = edge;
    }
    
    //Get the left neighbour of the edge.
    public Integer getLeft()
    {
        return this.edge.getFirst();
    }
    
    //Get the right neighbour of the edge.
    public Integer getRight() 
    {
        return this.edge.getSecond();
    }    
    
    //
    public Edge reverse()
    {
        Integer first = this.edge.getFirst();
        Integer second = this.edge.getSecond();
        
        Edge edge = new Edge(new Pair(second, first));
        
        return edge;
    }
}
