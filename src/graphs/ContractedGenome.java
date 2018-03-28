package graphs;

import structs.Genome;
import com.google.common.graph.*;

import java.util.ArrayList;

public class ContractedGenome extends Graph {

    public ContractedGenome(){
        graph = NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build();
        numEdges = 0;
    }

    public ContractedGenome(Genome order){
        graph = NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build();
        numEdges = 0;

        addGenome(order);
    }

    private ArrayList<String> getLabels(String gene){
        ArrayList<String> labels = new ArrayList<>();
        if (gene.charAt(0) == '-') {
            String name = gene.substring(1);
            labels.add(name + "h"); labels.add(name + "t");
        }else{
            labels.add(gene + "t"); labels.add(gene + "h");
        }

        return labels;
    }

    public void addGenome(Genome order){
        for (int i = 0; i < order.getSize(); ++i) {
            ArrayList<String> genes = order.getChromosome(i).getGenes();
            ArrayList<String> uLabels, vLabels;

            for (int j = 0; j < genes.size() - 1; ++j) {
                uLabels = getLabels(genes.get(j));
                vLabels = getLabels(genes.get(j+1));

                addEdge(uLabels.get(1), vLabels.get(0));
            }
            if (order.getChromosome(i).isCyclical()) {
                uLabels = getLabels(genes.get(0));
                vLabels = getLabels(genes.get(genes.size() - 1));
                addEdge(uLabels.get(0), vLabels.get(1));
            }else {
                //TODO: Add support for Linear Chromosomes
                throw new UnsupportedOperationException();
            }
        }
    }

}
