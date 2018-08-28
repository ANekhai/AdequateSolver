package graphs;

import com.google.common.graph.*;
import genome.Genome;

import java.util.ArrayList;
import java.util.HashSet;

public class ContractedGraph extends Graph {

    public ContractedGraph(){
        graph = NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build();
    }

    public ContractedGraph(Genome order){
        graph = NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build();

        addGenome(order);
    }

    public ContractedGraph(Graph graph) {
        this.graph = NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build();

        // add edges in graph to new graph
        for (int edge : graph.getEdges()) {
            EndpointPair<String> nodes = graph.incidentNodes(edge);
            addEdge(nodes.nodeU(), nodes.nodeV());
        }
        this.duplicated = graph.duplicated;

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

    //TODO: Will need to be updated once linear chromosomes are
    public Genome toGeneOrder() {
        ArrayList<ArrayList<String>> chromosomes = new ArrayList<>();

        HashSet<String> available = new HashSet<>(this.getNodes());

        while (available.size() > 0) {
            ArrayList<String> chromosome = new ArrayList<>();

            String currNode = available.iterator().next();
            String firstNode = currNode;


            do {
                if (this.getDegree(currNode) > 1) {
                    throw new RuntimeException("Converting to order is ambiguous with duplicated genes");
                }

                chromosome.add(currNode);
                available.remove(currNode);
                currNode = getOppositeExtremity(currNode);
                chromosome.add(currNode);
                available.remove(currNode);
                currNode = getAdjacentNodes(currNode).iterator().next();

            } while ( !currNode.equals(firstNode));

            chromosomes.add(chromosome);

        }

        Genome order = new Genome();
        for (ArrayList<String> chromosome : chromosomes) {
            ArrayList<String> genes = new ArrayList<>();
            for (int i = 0; i < chromosome.size(); i += 2) {
                String extremity = chromosome.get(i);
                if (extremity.substring(extremity.length() - 1).equals("h")) {
                    extremity = extremity.substring(0, extremity.length() - 1);
                } else {
                    extremity = "-" + extremity.substring(0, extremity.length() - 1);
                }
                genes.add(extremity);

            }
            order.addChromosome(genes, true);
        }

        return order;
    }

}
