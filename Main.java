package MinimumSpanningTree;

import java.io.*;
import java.util.*;

public class Main implements Runnable
{
    private String fileName;
    private String algorithm;
    
    private File file;
    
    private LinkedList verticesInfo;
    private LinkedList edgesInfo;
    
    private LinkedList graphsInfo;
    
    private LinkedList graphs;
    
    public Main(String fileName,String algorithm)
    {
        this.fileName = fileName;
        this.algorithm = algorithm;
        
        verticesInfo = new LinkedList();
        edgesInfo = new LinkedList();
        
        graphsInfo = new LinkedList();
        
        graphs = new LinkedList();
    }
    
    public void run()
    {
        validate();
        loadFile();
        buildGraphsInfo();
        buildGraphs();
        printAll();
        algorithm();
        showResult();
    }
    
    private void validate()
    {
        String directory = System.getProperty("user.dir") + "/";
        
        file = new File(directory + fileName);
        
        if(!file.exists())
        {
            System.out.print("\n\tO ficheiro \"" + file.toString() + "\" nao foi encontrado\n");
            System.exit(1);
        }
        
        algorithm = algorithm.toLowerCase();
        
        if(!algorithm.equals("p") && !algorithm.equals("k"))
        {
            System.out.print("\n\tAlgoritmo invalido\n");
            System.exit(1);
        }
    }
    
    private void loadFile()
    {
        int line = 0;
        
        try
        {
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            
            String str;
            
            str = "";
            
            while(!str.equals("#"))
            {
                str = bufReader.readLine();
                line++;
                
                if(str == null)
                {
                    System.out.print("\n\tO ficheiro nao tem o formato correcto ( Linha " + line + " )\n");
                    System.exit(1);
                }
            }
            
            str = bufReader.readLine();
            line++;
            
            if(str == null)
            {
                System.out.print("\n\tO ficheiro nao tem o formato correcto ( Linha " + line + " )\n");
                System.exit(1);
            }
            
            while(!str.equals("#"))
            {
                StringTokenizer strTok = new StringTokenizer(str);
                
                if(strTok.countTokens() != 3)
                {
                    System.out.print("\n\tO ficheiro nao tem o formato correcto ( Linha " + line + " )\n");
                    System.exit(1);
                }
                
                String s1 = strTok.nextToken();
                String s2 = strTok.nextToken();
                
                if(!checkString(s1))
                {
                    System.out.print("\n\tO ficheiro nao tem o formato correcto ( Linha " + line + " )\n");
                    System.exit(1);
                }
                
                if(!checkString(s2))
                {
                    System.out.print("\n\tO ficheiro nao tem o formato correcto ( Linha " + line + " )\n");
                    System.exit(1);
                }
                
                boolean f1 = true;
                boolean f2 = true;
                
                for(ListIterator listItr = verticesInfo.listIterator() ; listItr.hasNext() ; )
                {
                    VertexInfo vInfo = (VertexInfo) listItr.next();
                    
                    if(vInfo.name.equals(s1)) f1 = false;
                    if(vInfo.name.equals(s2)) f2 = false;
                }
                
                if(f1) verticesInfo.addLast(new VertexInfo(s1));
                if(f2 && !s2.equals(s1)) verticesInfo.addLast(new VertexInfo(s2));
                
                int weight = 0;
                
                try
                {
                    weight = Integer.parseInt(strTok.nextToken());
                }
                catch(NumberFormatException nfe)
                {
                    System.out.print("\n\tO ficheiro nao tem o formato correcto ( Linha " + line + " )\n");
                    System.exit(1);
                }
                
                edgesInfo.addLast(new EdgeInfo(s1,s2,weight));
                
                str = bufReader.readLine();
                line++;
                
                if(str == null)
                {
                    System.out.print("\n\tO ficheiro nao tem o formato correcto ( Linha " + line + " )\n");
                    System.exit(1);
                }
            }
            
            bufReader.close();
        }
        catch(Exception e)
        {
            System.out.print("\n" + e.toString() + "\n");
            System.exit(1);
        }
    }
    
    private void buildGraphsInfo()
    {
        GraphInfo gInfo;
        
        for(ListIterator listItr = edgesInfo.listIterator() ; listItr.hasNext() ; )
        {
            EdgeInfo eInfo = (EdgeInfo) listItr.next();
            
            int index1 = indexOf(eInfo.v1Name);
            int index2 = indexOf(eInfo.v2Name);
            
            if(index1 == index2)
            {
                if(index1 == -1)
                {
                    gInfo = new GraphInfo();

                    gInfo.verticesInfo.addLast(new VertexInfo(eInfo.v1Name));
                    if(!eInfo.v2Name.equals(eInfo.v1Name)) gInfo.verticesInfo.addLast(new VertexInfo(eInfo.v2Name));
                    gInfo.edgesInfo.addLast(eInfo);

                    graphsInfo.addLast(gInfo);
                }
                else
                {
                    gInfo = (GraphInfo) graphsInfo.get(index1);
                    
                    gInfo.edgesInfo.addLast(eInfo);
                }
            }
            else
            {
                if(index1 == -1)
                {
                    gInfo = (GraphInfo) graphsInfo.get(index2);
                    
                    gInfo.verticesInfo.addLast(new VertexInfo(eInfo.v1Name));
                    gInfo.edgesInfo.addLast(eInfo);
                }
                else if(index2 == -1)
                {
                    gInfo = (GraphInfo) graphsInfo.get(index1);
                    
                    gInfo.verticesInfo.addLast(new VertexInfo(eInfo.v2Name));
                    gInfo.edgesInfo.addLast(eInfo);
                }
                else
                {
                    gInfo = (GraphInfo) graphsInfo.get(index1);
                    
                    GraphInfo gInfoAux = (GraphInfo) graphsInfo.get(index2);
                    
                    gInfo.verticesInfo.addAll(gInfoAux.verticesInfo);
                    gInfo.edgesInfo.addAll(gInfoAux.edgesInfo);
                    gInfo.edgesInfo.addLast(eInfo);
                    
                    graphsInfo.remove(gInfoAux);
                }
            }
        }
        
        verticesInfo.clear();
        edgesInfo.clear();
        System.gc();
    }
    
    private void buildGraphs()
    {
        for(ListIterator listItr = graphsInfo.listIterator() ; listItr.hasNext() ; )
        {
            GraphInfo gInfo = (GraphInfo) listItr.next();
            
            Graph graph = new Graph();
            
            for(ListIterator listItr1 = gInfo.verticesInfo.listIterator() ; listItr1.hasNext() ; )
            {
                try
                {
                    graph.addVertex( ((VertexInfo) listItr1.next()).name );
                }
                catch(AlreadyAddedException e1)
                {
                    System.out.print("\n\tDeclaracao duplicada de um vertice\n");
                    System.exit(1);
                }
            }
            
            for(ListIterator listItr2 = gInfo.edgesInfo.listIterator() ; listItr2.hasNext() ; )
            {
                EdgeInfo eInfo = (EdgeInfo) listItr2.next();
                
                try
                {
                    graph.addEdge(eInfo.v1Name,eInfo.v2Name,eInfo.weight);
                }
                catch(EqualVerticesException e2)
                {
                    System.out.print("\n\tDeclarao de uma ligacao de um vertice para si proprio ( " + eInfo.v1Name + " <-> " + eInfo.v2Name + " )\n");
                    System.exit(1);
                }
                catch(NoSuchVertexException e3)
                {
                    System.out.print("\n\tDeclaracao de uma ligacao usando um vertice nao existente ( " + eInfo.v1Name + " <-> " + eInfo.v2Name + " )\n");
                    System.exit(1);
                }
                catch(NegativeWeightException e4)
                {
                    System.out.print("\n\tDeclaracao de uma ligacao usando um peso negativo ( " + eInfo.v1Name + " <-> " + eInfo.v2Name + " )\n");
                    System.exit(1);
                }
                catch(AlreadyConnectedException e5)
                {
                    System.out.print("\n\tDeclaracao duplicada de uma ligacao ( " + eInfo.v1Name + " <-> " + eInfo.v2Name + " )\n");
                    System.exit(1);
                }
            }
            
            graphs.addLast(graph);
        }
        
        graphsInfo.clear();
        System.gc();
    }
    
    private void printAll()
    {
        int i = 1;
        
        for(ListIterator listItr = graphs.listIterator() ; listItr.hasNext() ; )
        {
            Graph graph = (Graph) listItr.next();
            
            System.out.print("\n\tGrafo " + i + "\n\n");
            System.out.print(graph.toString());
            
            i++;
        }
    }
    
    private void algorithm()
    {
        int i = 1;
        
        ListIterator listItr;
        Graph graph;
        
        if(algorithm.equals("p"))
            for(listItr = graphs.listIterator() ; listItr.hasNext() ; )
            {
                System.out.print("\n\tA executar o Algoritmo de Prim no grafo " + i + "...");
                
                graph = (Graph) listItr.next();
                graph.prim();
                
                i++;
            }
        else if(algorithm.equals("k"))
            for(listItr = graphs.listIterator() ; listItr.hasNext() ; )
            {
                System.out.print("\n\tA executar o Algoritmo de Kruskal no grafo " + i + "...");
                
                graph = (Graph) listItr.next();
                graph.kruskal();
                
                i++;
            }
        
        System.out.print("\n");
    }
    
    private void showResult()
    {
        int i = 1;
        
        for(ListIterator listItr = graphs.listIterator() ; listItr.hasNext() ; )
        {
            Graph graph = (Graph) listItr.next();
            
            System.out.print("\n\tGrafo " + i + " - Arvore de expansao minima\n\n");
            System.out.print(graph.getAlgorithmResult());
            
            i++;
        }
    }
    
    private boolean checkString(String s)
    {
        boolean b = true;
        
        if(s.length() == 0) b = false;
        else for(int i = 0 ; i < s.length() ; i++)
            if(!Character.isLetter(s.charAt(i)) && !Character.isDigit(s.charAt(i)))
            {
                b = false;
                break;
            }
        
        return b;
    }
    
    private int indexOf(String vertexName)
    {
        int index = -1;
        
        int i = 0;
        
        boolean flag = false;
        
        for(ListIterator listItr1 = graphsInfo.listIterator() ; listItr1.hasNext() ; )
        {
            GraphInfo gInfo = (GraphInfo) listItr1.next();
            
            for(ListIterator listItr2 = gInfo.verticesInfo.listIterator() ; listItr2.hasNext() ; )
            {
                VertexInfo vInfo = (VertexInfo) listItr2.next();
                
                if(vInfo.name.equals(vertexName))
                {
                    index = i;
                    flag = true;
                    break;
                }
            }
            
            if(flag) break;
            
            i++;
        }
        
        return index;
    }
    
    private class VertexInfo
    {
        String name;
        
        public VertexInfo(String name)
        {
            this.name = name;
        }
    }
    
    private class EdgeInfo
    {
        String v1Name;
        String v2Name;
        
        int weight;
        
        public EdgeInfo(String v1Name,String v2Name,int weight)
        {
            this.v1Name = v1Name;
            this.v2Name = v2Name;
            
            this.weight = weight;
        }
    }
    
    private class GraphInfo
    {
        LinkedList verticesInfo;
        LinkedList edgesInfo;
        
        public GraphInfo()
        {
            verticesInfo = new LinkedList();
            edgesInfo = new LinkedList();
        }
    }
    
    public static void main(String args[])
    {
        if(args.length != 2)
        {
            System.out.print("\n\tUso: Main [ Ficheiro de entrada ] [ Algoritmo ]\n");
            System.out.print("\n\t[ Algoritmo ]\n");
            System.out.print("\n\tP - Algoritmo de Prim");
            System.out.print("\n\tK - Algoritmo de Kruskal\n");
            System.exit(1);
        }
        
        new Thread(new Main(args[0],args[1])).start();
    }
}