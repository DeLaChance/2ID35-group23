package labeler;

/**
 * TODO: Documentation
 */
public class GraphEdge {
    private Pair<Integer,Integer> edge;
    
    //Constructor which initialize the edge.
    public GraphEdge(Pair<Integer,Integer> edge)
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
    public GraphEdge reverse()
    {
        Integer first = this.edge.getFirst();
        Integer second = this.edge.getSecond();
        
        GraphEdge edge = new GraphEdge(new Pair(second, first));
        
        return edge;
    }
    
    @Override
    public String toString()
    {
        return "(" + this.edge.getFirst() + "," + this.edge.getSecond() + ")";
    }
}
