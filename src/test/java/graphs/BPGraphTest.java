package graphs;

import genome.Genome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BPGraphTest {
    private BPGraph bpGraph;
    private ContractedGraph contracted;
    private NonContractedGraph nonContracted;

    private String endLine = System.getProperty("line.separator");

    @BeforeEach
    void setUp() {
        ArrayList<String> genes = new ArrayList<>();
        genes.add("1"); genes.add("2");
        Genome genome = new Genome("test", true, genes);
        contracted = new ContractedGraph(genome);
        nonContracted = new NonContractedGraph(genome);
        bpGraph = new BPGraph();
    }

    @Test
    void testBufferedConstructor() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "1 2 3 @" + endLine + ">Three" + endLine + "1 2 3 @"));

        bpGraph = new BPGraph(in);

        assertEquals(3, bpGraph.getColorsSize());
        assertEquals(9, bpGraph.getGeneNumber());

    }

    @Test
    void testMalformedStringInBufferedConstructor() {

    }

    @Test
    void testBPGraphFromContractedGraphs() {
        bpGraph = new BPGraph(contracted, contracted, contracted);

        assertEquals(3, bpGraph.getColorsSize());
        assertTrue(bpGraph.isContracted());
    }

    @Test
    void testBPGraphFromNonContractedGraphs() {
        bpGraph = new BPGraph(nonContracted, nonContracted, nonContracted);

        assertEquals(3, bpGraph.getColorsSize());
        assertFalse(bpGraph.isContracted());
    }

    @Test
    void testAddingContractedGraphToEmptyBPGraph() {
        bpGraph.add(contracted);
        assertTrue(bpGraph.isContracted());
    }

    @Test
    void testAddingNonContractedGraphToEmptyBPGraph() {
        bpGraph.add(nonContracted);
        assertFalse(bpGraph.isContracted());
    }

    @Test
    void testAddingMixedGraphsToBPGraphFails() {
        bpGraph.add(contracted);
        assertThrows(RuntimeException.class, ()->bpGraph.add(nonContracted));
    }

    //TODO: REWRITE THIS TEST AS BPGRAPH REQUIRES EQUAL GENE CONTENT RIGHT NOW
    @Test
    void testConnectedEdgeChecking() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 @" + endLine +
                ">Two" + endLine + "-2 @" + endLine + ">Three" + endLine + "3 @"));

        bpGraph = new BPGraph(in);

        assertTrue(bpGraph.isConnected("1t", "1h") && bpGraph.isConnected("2h", "2t") &&
                bpGraph.isConnected("3h", "3t"));

        assertFalse(bpGraph.isConnected("1t", "2h"));
        assertFalse(bpGraph.isConnected("4h", "4t"));

    }

    @Test
    void testShrinkWithOneEdge() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 4 @" + endLine +
                ">Two" + endLine + "1 @" + endLine + "2 @"+ endLine + "3 @" + endLine + "4 @" ));

        bpGraph = new BPGraph(in);

        ArrayList<String> foundSubgraphs = new ArrayList<>();
        foundSubgraphs.add("1h"); foundSubgraphs.add("2t");

        bpGraph.shrink(foundSubgraphs, 0, 1);

        assertTrue(bpGraph.getAdjacencyInColor("1t", 1).contains("2h"));
        // TODO: There are problems with how the edge number is updated I think (at least with start = 0, end = 1
        // assertEquals(7, bpGraph.getEdgeNumber());
        assertEquals(1, bpGraph.getCycleNumber());
        assertEquals(2, bpGraph.getFootprintSize());

    }

    @Test
    void testShrinkWithTwoEdges() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 4 @" + endLine +
                ">Two" + endLine + "1 @" + endLine + "2 @"+ endLine + "3 @" + endLine + "4 @" ));

        bpGraph = new BPGraph(in);

        ArrayList<String> foundSubgraphs = new ArrayList<>();
        foundSubgraphs.add("1h"); foundSubgraphs.add("2t");

        bpGraph.shrink(foundSubgraphs, 0, 1);

        assertTrue(bpGraph.getAdjacencyInColor("1t", 1).contains("2h"));
        // TODO: There are problems with how the edge number is updated I think (at least with start = 0, end = 1
        // assertEquals(7, bpGraph.getEdgeNumber());
        assertEquals(1, bpGraph.getCycleNumber());
        assertEquals(2, bpGraph.getFootprintSize());
    }

    @Test
    void testCycleCounting() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "1 -2 3 @" ));

        bpGraph = new BPGraph(in);
        bpGraph.countCycles(0, 1);

        assertEquals(2, bpGraph.getCycle(0));

    }

    @Test
    void testSettingBoundsWithIdenticalGenomes() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "1 2 3 @" + endLine + ">Three" + endLine + "1 2 3 @"));

        bpGraph = new BPGraph(in);
        bpGraph.getBounds();

        assertEquals( 15, bpGraph.getLowerBound());
        assertEquals(18, bpGraph.getUpperBound());
    }

    @Test
    void testSettingBoundsWithDifferentGenomes() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "1 -2 3 @" + endLine + ">Three" + endLine + "1 -3 2 @"));

        bpGraph = new BPGraph(in);
        bpGraph.getBounds();

        assertEquals(13, bpGraph.getLowerBound());
        assertEquals(16, bpGraph.getUpperBound());

    }

    //TODO: TEST CHANGING AVAILABILITY


}