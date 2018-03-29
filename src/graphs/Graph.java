package graphs;

import com.google.common.graph.*;
import structs.Genome;

import java.util.Set;

public abstract class Graph {
    protected MutableNetwork<String, Integer> graph;
    protected int numEdges = 0;

    abstract void addGenome(Genome order);

    public void addEdge(String u, String v){
        ++numEdges;
        graph.addEdge(u, v, numEdges);
    }

    //Getters

    Set<String> getNodes() { return graph.nodes(); }

    Integer getDegree(String node) { return graph.degree(node); }

    Set<Integer> incidentEdges(String node) { return graph.incidentEdges(node); }

    Set<String> getAdjacentNodes(String node) { return graph.adjacentNodes(node); }

    Set<Integer> getEdgesConnecting(String nodeU, String nodeV) { return graph.edgesConnecting(nodeU, nodeV); }

    ElementOrder<String> getNodeOrder() { return graph.nodeOrder(); }

    ElementOrder<Integer> getEdgeOrder() { return graph.edgeOrder(); }

    //Member Functions

    boolean contains(String node){
        return graph.nodes().contains(node);
    }

    boolean hasEdge(String nodeU, String nodeV) { return graph.hasEdgeConnecting(nodeU, nodeV); }

    boolean equals(Graph otherGraph) { return graph.equals(otherGraph); }

    private String getAdjacentExtremity(String node) {
        if (node.indexOf('h') != -1){
            return node.replace('h', 't');
        }else {
            return node.replace('t', 'h');
        }
    }

    abstract Genome toGeneOrder();

}
