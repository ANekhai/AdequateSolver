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

class AS1Test {
    AS1 detector = new AS1();
    BPGraph graph;


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

    @Test
    void testGraphWithNoAS1s() {
        addGraphFromGenes("1");
        addGraphFromGenes("2");
        addGraphFromGenes("3");

        detector.AS1(graph);

        assertEquals(0, detector.numDetected);

    }

    @Test
    void testGraphWithTwoColorAS1() {
        addGraphFromGenes("1");
        addGraphFromGenes("1");
        addGraphFromGenes("2");

        detector.AS1(graph);

        assertEquals(1, detector.numDetected);
        assertEquals(2,detector.foundSubgraphs.size());

    }

    @Test
    void testGraphWithThreeColorAS1() {
        addGraphFromGenes("1");
        addGraphFromGenes("1");
        addGraphFromGenes("1");

        detector.AS1(graph);

        // should detect an 2 nodes in an AS for each
        assertEquals(1, detector.numDetected);
        assertEquals(6, detector.foundSubgraphs.size());

    }

}