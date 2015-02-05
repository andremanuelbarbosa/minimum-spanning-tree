package MinimumSpanningTree;

public class AlreadyConnectedException extends RuntimeException
{
    public AlreadyConnectedException()
    {
        super();
    }
    
    public AlreadyConnectedException(String s)
    {
        super(s);
    }
}