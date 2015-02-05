package MinimumSpanningTree;

public class NoSuchVertexException extends RuntimeException
{
    public NoSuchVertexException()
    {
        super();
    }
    
    public NoSuchVertexException(String s)
    {
        super(s);
    }
}