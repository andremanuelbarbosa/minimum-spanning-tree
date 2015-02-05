package MinimumSpanningTree;

import java.util.*;

public class Graph
{
    private static final int PRIM = 1;
    private static final int KRUSKAL = 2;
    
    private int algorithm = -1;
    
    private HashMap vertices;
    private LinkedList edges;
    
    private LinkedList edgesKruskal;
    
    public Graph()
    {
        vertices = new HashMap();
        edges = new LinkedList();
        
        edgesKruskal = new LinkedList();
    }
    
    public void addVertex(String name)
    {
        if( ((Vertex) vertices.get(name)) != null ) throw new AlreadyAddedException();
        
        vertices.put(name,new Vertex(name));
    }
    
    public void addEdge(String v1Name,String v2Name,int weight)
    {
        if(v1Name.equals(v2Name)) throw new EqualVerticesException();
        
        Vertex vertex1 = (Vertex) vertices.get(v1Name);
        Vertex vertex2 = (Vertex) vertices.get(v2Name);
        
        if(vertex1 == null || vertex2 == null) throw new NoSuchVertexException();
        
        if(weight < 0) throw new NegativeWeightException();
        
        for(ListIterator listItr = vertex1.edges.listIterator() ; listItr.hasNext() ; )
        {
            Edge e = (Edge) listItr.next();
            
            if(e.v1.equals(vertex1) && e.v2.equals(vertex2)) throw new AlreadyConnectedException();
            if(e.v2.equals(vertex1) && e.v1.equals(vertex2)) throw new AlreadyConnectedException();
        }
        
        Edge edge = new Edge(vertex1,vertex2,weight);
        
        vertex1.edges.addLast(edge);
        vertex2.edges.addLast(edge);
        
        edges.addLast(edge);
    }
    
    public void prim()
    {
        int numberOfVertices = vertices.size();
        
        if(numberOfVertices == 0) return;
        
        algorithm = PRIM;
        
        resetAll();
        
        LinkedList verticesToProcess = new LinkedList();
        
        Vertex v = (Vertex) vertices.values().iterator().next();
        
        v.known = true;
        
        verticesToProcess.addLast(v);
        
        while(verticesToProcess.size() < numberOfVertices)
        {
            Edge e = selectEdgePrim(verticesToProcess);
            
            if(!e.v1.known)
            {
                e.v1.known = true;
                e.v1.previous = e.v2;
                e.v1.weight = e.weight;
                
                verticesToProcess.addLast(e.v1);
            }
            else
            {
                e.v2.known = true;
                e.v2.previous = e.v1;
                e.v2.weight = e.weight;
                
                verticesToProcess.addLast(e.v2);
            }
        }
    }
    
    public void kruskal()
    {
        int numberOfVertices = vertices.size();
        
        if(numberOfVertices == 0) return;
        
        algorithm = KRUSKAL;
        
        int edgesAccepted = 0;
        
        LinkedList edgesToProcess = new LinkedList(edges);
        
        LinkedList trees = new LinkedList();
        
        LinkedList tree;
        LinkedList treeAux;
        
        edgesKruskal.clear();
        
        while(edgesAccepted < vertices.size() - 1)
        {
            Edge e = null;
            
            while(true)
            {
                e = selectEdgeKruskal(edgesToProcess);
                
                int index1 = indexOf(trees,e.v1);
                int index2 = indexOf(trees,e.v2);
                
                if(index1 == index2)
                {
                    if(index1 == -1)
                    {
                        edgesKruskal.addLast(e);
                        
                        tree = new LinkedList();
                        
                        tree.addLast(e.v1);
                        tree.addLast(e.v2);
                        
                        trees.addLast(tree);
                        
                        edgesAccepted++;
                        break;
                    }
                }
                else
                {
                    if(index1 == -1)
                    {
                        edgesKruskal.addLast(e);
                        
                        tree = (LinkedList) trees.get(index2);
                        tree.addLast(e.v1);
                        
                        edgesAccepted++;
                        break;
                    }
                    else if(index2 == -1)
                    {
                        edgesKruskal.addLast(e);
                        
                        tree = (LinkedList) trees.get(index1);
                        tree.addLast(e.v2);
                        
                        edgesAccepted++;
                        break;
                    }
                    else
                    {
                        edgesKruskal.addLast(e);
                        
                        tree = (LinkedList) trees.get(index1);
                        treeAux = (LinkedList) trees.get(index2);
                        
                        tree.addAll(treeAux);
                        
                        trees.remove(treeAux);
                        
                        edgesAccepted++;
                        break;
                    }
                }
            }
        }
    }
    
    public String getAlgorithmResult()
    {
        String result = "";
        
        if(algorithm == PRIM)
        {
            for(Iterator itr = vertices.values().iterator() ; itr.hasNext() ; )
            {
                Vertex v = (Vertex) itr.next();
                
                if(v.previous != null)
                    result += "\t" + v.name + " <-> " + v.previous.name + " ( " + v.weight + " )\n";
            }
        }
        else if(algorithm == KRUSKAL)
        {
            for(ListIterator listItr = edgesKruskal.listIterator() ; listItr.hasNext() ; )
            {
                Edge e = (Edge) listItr.next();

                result += "\t" + e.v1.name + " <-> " + e.v2.name + " ( " + e.weight + " )\n";
            }
        }
        
        return result;
    }
    
    public String toString()
    {
        String s = "";
        
        for(ListIterator listItr = edges.listIterator() ; listItr.hasNext() ; )
        {
            Edge e = (Edge) listItr.next();
            
            s += "\t" + e.v1.name + " <-> " + e.v2.name + " ( " + e.weight + " )\n";
        }
        
        return s;
    }
    
    private void resetAll()
    {
        for(Iterator itr = vertices.values().iterator() ; itr.hasNext() ; )
        {
            Vertex v = (Vertex) itr.next();
            
            v.known = false;
            v.previous = null;
            v.weight = 0;
        }
    }
    
    private Edge selectEdgePrim(LinkedList verticesToProcess)
    {
        Edge edge = null;
        int weight = Integer.MAX_VALUE;
        
        for(ListIterator listItr1 = verticesToProcess.listIterator() ; listItr1.hasNext() ; )
        {
            Vertex v = (Vertex) listItr1.next();
            
            for(ListIterator listItr2 = v.edges.listIterator() ; listItr2.hasNext() ; )
            {
                Edge e = (Edge) listItr2.next();
                
                if(e.weight < weight && !(e.v1.known && e.v2.known))
                {
                    edge = e;
                    weight = e.weight;
                }
            }
        }
        
        return edge;
    }
    
    private Edge selectEdgeKruskal(LinkedList edgesToProcess)
    {
        Edge edge = null;
        int weight = Integer.MAX_VALUE;
        
        for(ListIterator listItr = edgesToProcess.listIterator() ; listItr.hasNext() ; )
        {
            Edge e = (Edge) listItr.next();
            
            if(e.weight < weight)
            {
                edge = e;
                weight = e.weight;
            }
        }
        
        edgesToProcess.remove(edge);
        
        return edge;
    }
    
    private int indexOf(LinkedList trees,Vertex vertex)
    {
        int index = -1;
        
        int i = 0;
        boolean f = false;
        
        for(ListIterator listItr1 = trees.listIterator() ; listItr1.hasNext() ; )
        {
            LinkedList tree = (LinkedList) listItr1.next();
            
            for(ListIterator listItr2 = tree.listIterator() ; listItr2.hasNext() ; )
            {
                Vertex v = (Vertex) listItr2.next();
                
                if(vertex.equals(v))
                {
                    index = i;
                    f = true;
                    break;
                }
            }
            
            if(f) break;
            
            i++;
        }
        
        return index;
    }
    
    private class Vertex
    {
        String name;
        LinkedList edges;
        
        boolean known;
        Vertex previous;
        int weight;
        
        public Vertex(String name)
        {
            this.name = name;
            edges = new LinkedList();
        }
    }
    
    private class Edge
    {
        Vertex v1;
        Vertex v2;
        
        int weight;
        
        public Edge(Vertex v1,Vertex v2,int weight)
        {
            this.v1 = v1;
            this.v2 = v2;
            
            this.weight = weight;
        }
    }
}