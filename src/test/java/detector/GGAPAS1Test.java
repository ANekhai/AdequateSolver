package detector;

import genome.Genome;
import graphs.BPGraph;
import graphs.ContractedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GGAPAS1Test {
    private GGAPAS1 detector = new GGAPAS1();
    private BPGraph graph;

    @BeforeEach
    private void setUp() {
        graph = new BPGraph();
    }

    private void addGraphFromGenes(String... genes) {
        ArrayList<String> geneArray = new ArrayList<>(Arrays.asList(genes));
        Genome order = new Genome("test", true, geneArray);
        ContractedGraph genomeGraph = new ContractedGraph(order);
        graph.add(genomeGraph);

    }

    @Test
    void testGraphWithDuplicatedEarSubgraph() {
        addGraphFromGenes("1", "-1");
        addGraphFromGenes("1");

        assertTrue(detector.AS1(graph));

        assertEquals(1, detector.numDetected);
    }

    @Test
    void testGraphWithParallelEdgesInColor() {
        addGraphFromGenes("1", "1");
        addGraphFromGenes("1");

        assertTrue(detector.AS1(graph));

        assertEquals(1, detector.numDetected);

    }

}