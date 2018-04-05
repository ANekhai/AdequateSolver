package graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class BPGraph {
    ArrayList<Graph> subGraphs;
    //one idea, store type of graph contained in subGraphs vector
    boolean isContracted;
    HashMap<String, Boolean> availableVertices;

    public BPGraph(){
        subGraphs = new ArrayList<>();
    }

    public BPGraph(ContractedGenome... graphs) {
        isContracted = true;
        subGraphs = new ArrayList<>();
        subGraphs.addAll(Arrays.asList(graphs));
        addAvailabilities(graphs);
    }

    public BPGraph(NonContractedGenome... graphs) {
        isContracted = false;
        subGraphs = new ArrayList<>();
        subGraphs.addAll(Arrays.asList(graphs));
        addAvailabilities(graphs);
    }

    //Getters
    public Set<String> getNodes() { return availableVertices.keySet(); }



    //Member functions
    public void add(Graph graph) {
        if (subGraphs.isEmpty()){
            isContracted = graph instanceof ContractedGenome;
        } else if ((graph instanceof NonContractedGenome && isContracted) ||
                (graph instanceof ContractedGenome && !isContracted) ) {
            //perhaps modify this to throw a checked exception instead
            throw new UnsupportedOperationException("Breakpoint graph must consist of the same genome graph type");
        }
        subGraphs.add(graph);
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

}
