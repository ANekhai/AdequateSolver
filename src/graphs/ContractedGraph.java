package graphs;

import structs.Genome;
import org.jgrapht.graph.*;

public class ContractedGraph extends Graph {

    ContractedGraph(){
        graph = new Pseudograph<String, DefaultEdge>(DefaultEdge.class);
        numEdges = 0;
        numVertices = 0;
    }

    ContractedGraph(Genome order){

    }

    private void addGenome(){
        return;
    }

    public void addVertex(String vertex){

    }

    public void addEdge(String source, String target){

    }



}
