package detector;

import graphs.BPGraph;
import graphs.ContractedGraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structs.Genome;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AS0Test {
    AS0 detector = new AS0();
    BPGraph graph;

    private void addGraphFromGenes(String... genes) {
        ArrayList<String> geneArray = new ArrayList<>(Arrays.asList(genes));
        Genome order = new Genome("test", true, geneArray);
        ContractedGraph genomeGraph = new ContractedGraph(order);
        graph.add(genomeGraph);

    }

    @BeforeEach
    void setUp() {
        graph = new BPGraph();

        addGraphFromGenes("1", "2");
        addGraphFromGenes("1", "3");
        addGraphFromGenes("1", "4");

    }

    @Test
    void testCircularDetectionWithAllFreeNodes() {
        detector.AS0(graph);

        // Equal to number of nodes minus one
        assertEquals(7, detector.numDetected);
    }

    @Test
    void testCircularDetectionWithOneUsedNodes() {
        graph.markUsedNode("4t");
        detector.AS0(graph);

        // equal to number of nodes - 2
        assertEquals(6, detector.numDetected);
    }

    @Test
    void testCircularDetectionWithOneAvailableNode() {
        graph.markUsedNode("4t", "4h", "3t", "3h", "2t", "2h", "1t");
        detector.AS0(graph);

        assertEquals(0, detector.numDetected);
    }

    //TODO: Add test cases for self loops when duplicated chromosomes start getting considered

}