package detector;

import graphs.BPGraph;
import graphs.ContractedGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structs.Genome;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AS4Test {
    BPGraph graph;
    AS4 detector = new AS4();

    @BeforeEach
    void setUp() {
        graph = new BPGraph();
    }

    @AfterEach
    void tearDown() {
        detector.clean();
    }

    private void addGraphFromGenes(String... genes) {
        ArrayList<String> geneArray = new ArrayList<>(Arrays.asList(genes));
        Genome order = new Genome("test", true, geneArray);
        ContractedGraph genomeGraph = new ContractedGraph(order);
        graph.add(genomeGraph);
    }

    private ContractedGraph getGenomeWithSingleCircularChromosomes(String... genes) {
        Genome genome = new Genome();
        ArrayList<String> chromosome = new ArrayList<>();

        for (String gene : genes) {
            chromosome.add(gene);
            genome.addChromosome(chromosome, true);
        }

        return new ContractedGraph(genome);
    }

    @Test
    void testOnlyAS1s() {
        addGraphFromGenes("1", "2");
        addGraphFromGenes("1", "2");
        addGraphFromGenes("1", "2");

        detector.AS4(graph);

        assertEquals(0, detector.numDetected);

    }

    @Test
    void testOnlyAS2s() {
        addGraphFromGenes("1", "2");
        addGraphFromGenes("1", "-2");
        addGraphFromGenes("1", "2"); // Throw away gene

        detector.AS4(graph);

        assertEquals(0, detector.numDetected);
    }



}