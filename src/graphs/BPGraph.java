package graphs;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class BPGraph {
    ArrayList<Graph> colors;
    //one idea, store type of graph contained in colors array
    boolean isContracted;
    HashMap<String, Boolean> availableVertices;

    public BPGraph(){
        colors = new ArrayList<>();
    }

    public BPGraph(ContractedGenome... graphs) {
        isContracted = true;
        colors = new ArrayList<>();
        colors.addAll(Arrays.asList(graphs));
        addAvailabilities(graphs);
    }

    public BPGraph(NonContractedGenome... graphs) {
        isContracted = false;
        colors = new ArrayList<>();
        colors.addAll(Arrays.asList(graphs));
        addAvailabilities(graphs);
    }

    //Getters
    public Set<String> getNodes() { return availableVertices.keySet(); }

    public ArrayList<Set<String>> getAllAdjacencies(String node) {
        ArrayList<Set<String>> adjacencies = new ArrayList<>();
        for (Graph color : colors) {
            adjacencies.add(color.getAdjacentNodes(node));
        }
        return adjacencies;
    }

    public int getColorsSize() { return colors.size(); }

    public Set<String> getNodesInColor(int color) { return colors.get(color).getNodes(); }

    public Set<String> getAdjacencyInColor(String node, int color) { return colors.get(color).getAdjacentNodes(node); }

    public String getFirstAdjacency(String node, int color) {
        return colors.get(color).getAdjacentNodes(node).iterator().next();
    }

    //Member functions
    public void add(Graph graph) {
        if (colors.isEmpty()){
            isContracted = graph instanceof ContractedGenome;
        } else if ((graph instanceof NonContractedGenome && isContracted) ||
                (graph instanceof ContractedGenome && !isContracted) ) {
            //perhaps modify this to throw a checked exception instead
            throw new UnsupportedOperationException("Breakpoint graph must consist of the same genome graph type");
        }
        colors.add(graph);
        addAvailabilities(graph);
    }

    private void addAvailabilities(Graph... graphs) {
        for (Graph graph : graphs) {
            addAvailabilities(graph);
        }
    }

    private void addAvailabilities(Graph graph) {
        for (String node : graph.getNodes()) {
            availableVertices.putIfAbsent(node, true);
        }
    }

    public boolean checkAvailable(String node){
        if (availableVertices.get(node) != null) {
            return availableVertices.get(node);
        } else {
            return false;
        }
    }

    public HashMap<String, Boolean> copyAvailability() { return (HashMap<String, Boolean>) availableVertices.clone(); }

    public boolean isConnected(String u, String v) {
        for (Graph graph : colors) {
            if (graph.hasEdge(u, v)) {
                return true;
            }
        }
        return false;
    }

}
