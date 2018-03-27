package graphs;

import com.google.common.graph.*;
import structs.Genome;

import java.util.Set;

public abstract class Graph {
    protected MutableNetwork<String, Integer> graph;
    protected int numEdges;

    abstract void addGenome(Genome order);

    public void addEdge(String u, String v){
        ++numEdges;
        graph.addEdge(u, v, numEdges);
    }

    boolean contains(String node){
        return graph.nodes().contains(node);
    }

//     Accessors
    Set<String> getNodes() { return graph.nodes(); }

    boolean hasEdge(String u, String v) { return graph.hasEdgeConnecting(u, v); }

}
