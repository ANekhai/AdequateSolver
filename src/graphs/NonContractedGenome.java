package graphs;

import com.google.common.graph.*;
import structs.Genome;

import java.util.HashMap;
import java.util.Vector;

public class NonContractedGenome extends Graph {
    private HashMap<String, Integer> geneEnum;

    public NonContractedGenome() {
        geneEnum = new HashMap<>();
        graph = NetworkBuilder.undirected().build();
        numEdges = 0;
    }

    public NonContractedGenome(Genome order) {
        geneEnum = new HashMap<>();
        graph = NetworkBuilder.undirected().build();
        numEdges = 0;

        addGenome(order);
    }

    private Vector<String> getLabels(String gene) {
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

        Vector<String> labels = new Vector<>();
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
            Vector<String> genes = order.getChromosome(i).getGenes();
            Vector<String> currLabels, nextLabels;
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

}
