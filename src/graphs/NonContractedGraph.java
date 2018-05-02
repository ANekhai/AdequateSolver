package graphs;

import com.google.common.graph.*;
import genome.Genome;

import java.util.HashMap;
import java.util.ArrayList;

public class NonContractedGraph extends Graph {
    private HashMap<String, Integer> geneEnum;

    public NonContractedGraph() {
        geneEnum = new HashMap<>();
        graph = NetworkBuilder.undirected().build();
    }

    public NonContractedGraph(Genome order) {
        geneEnum = new HashMap<>();
        graph = NetworkBuilder.undirected().build();

        addGenome(order);
    }

    private ArrayList<String> getLabels(String gene) {
        String name;
        if (gene.charAt(0) == '-') {
            name = gene.substring(1);
        } else {
            name = gene;
        }
        if (geneEnum.containsKey(name))
            geneEnum.put(name, geneEnum.get(name) + 1);
        else
            geneEnum.put(name, 0);

        ArrayList<String> labels = new ArrayList<>();
        if (gene.equals(name)) {
            labels.add(gene + "t" + geneEnum.get(gene).toString());
            labels.add(gene + "h" + geneEnum.get(gene).toString());
        } else {
            labels.add(name + "h" + geneEnum.get(name).toString());
            labels.add(name + "t" + geneEnum.get(name).toString());
        }

        return labels;
    }

    public void addGenome(Genome order){
        for (int i = 0; i < order.getSize(); ++i) {
            ArrayList<String> genes = order.getChromosome(i).getGenes();
            ArrayList<String> currLabels, nextLabels;
            currLabels = getLabels(genes.get(0));
            String leftExtremity = currLabels.get(0);
            for (int j = 1; j < genes.size(); ++j) {
                nextLabels = getLabels(genes.get(j));
                addEdge(currLabels.get(1), nextLabels.get(0));
                currLabels = nextLabels;
            }
            if (order.getChromosome(i).isCyclical()) {
                addEdge(leftExtremity, currLabels.get(1));
            } else {
                //TODO: Add support for Linear Chromosomes
                throw new UnsupportedOperationException();
            }
        }
    }

    public Genome toGeneOrder(){
        return null;
    }

}
