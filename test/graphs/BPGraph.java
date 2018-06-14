package graphs;

import java.util.ArrayList;
import java.util.Arrays;

public class BPGraph {
    ArrayList<Graph> subGraphs;
    //one idea, store type of graph contained in subGraphs vector
    boolean isContracted; //TODO: if using this route, must figure out how to set this variable when default constructor called

    public BPGraph(){
        subGraphs = new ArrayList<>();
    }

    public BPGraph(ContractedGenome... graphs) {
        isContracted = true;
        subGraphs = new ArrayList<>();
        subGraphs.addAll(Arrays.asList(graphs));
    }

    public BPGraph(NonContractedGenome... graphs) {
        isContracted = false;
        subGraphs = new ArrayList<>();
        subGraphs.addAll(Arrays.asList(graphs));
    }

    public void add(Graph graph) {
        if (subGraphs.isEmpty()){
            isContracted = graph instanceof ContractedGenome;
        } else if ((graph instanceof NonContractedGenome && isContracted) ||
                (graph instanceof ContractedGenome && !isContracted) ) {
            //perhaps modify this to throw a checked exception instead
            throw new UnsupportedOperationException("Breakpoint graph must consist of the same genome graph type");
        }
        subGraphs.add(graph);
    }

}
