package graphs;

import com.google.common.graph.*;
import structs.Genome;

import java.util.HashSet;
import java.util.Set;

public abstract class Graph {
    protected MutableNetwork<String, Integer> graph;
    protected int numEdges = 0;
    //TODO: availableNodes mirrors DetectorCir but I suspect there are problems with this when we have duplicated nodes

    abstract void addGenome(Genome order);

    public void addEdge(String u, String v) {
        graph.addEdge(u, v, numEdges);
        ++numEdges;
    }

    //Getters

    public Set<String> getNodes() { return graph.nodes(); }

    public int getNumNodes() { return graph.nodes().size(); }

    public int getNumEdges() { return numEdges; }

    public int getDegree(String node) { return graph.degree(node); }

    public Set<Integer> incidentEdges(String node) { return graph.incidentEdges(node); }

    public Set<String> getAdjacentNodes(String node) {
        try {
            return graph.adjacentNodes(node);
        } catch (IllegalArgumentException e) {
            return new HashSet<>();
        }
    }

    public Set<Integer> getEdgesConnecting(String nodeU, String nodeV) { return graph.edgesConnecting(nodeU, nodeV); }

    public ElementOrder<String> getNodeOrder() { return graph.nodeOrder(); }

    public ElementOrder<Integer> getEdgeOrder() { return graph.edgeOrder(); }

    //Member Functions

    public boolean contains(String node){
        return graph.nodes().contains(node);
    }

    public boolean hasEdge(String nodeU, String nodeV) { return graph.hasEdgeConnecting(nodeU, nodeV); }

    public boolean equals(Graph otherGraph) { return graph.equals(otherGraph); }

    private String getOppositeExtremity(String node) {
        if (node.indexOf('h') != -1){
            return node.replace('h', 't');
        }else {
            return node.replace('t', 'h');
        }
    }

    abstract Genome toGeneOrder();


    //TODO: Add Shrink function for graph

}
