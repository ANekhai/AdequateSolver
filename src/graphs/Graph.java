package graphs;

import org.jgrapht.graph.*;
import structs.Genome;

public abstract class Graph {
    protected Pseudograph<String, DefaultEdge> graph;
    protected int numEdges;
    protected int numVertices;

    abstract void addGenome(Genome order);

    abstract void addVertex(String vertex);

    public void addVertices(String... vertices){
        for (String vertex : vertices){
            addVertex(vertex);
        }
    }

    abstract void addEdge(String source, String target);

    // Accessors

    DefaultEdge getEdge(String source, String target){
        return 
    }



}
