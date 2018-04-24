package graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structs.Genome;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BPGraphTest {
    private BPGraph bpGraph;
    private ContractedGraph contracted;
    private NonContractedGraph nonContracted;

    @BeforeEach
    void setUp() {
        ArrayList<String> genes = new ArrayList<>();
        genes.add("1"); genes.add("2");
        Genome genome = new Genome("test", true, genes);
        contracted = new ContractedGraph(genome);
        nonContracted = new NonContractedGraph(genome);
    }

    @Test
    void testBPGraphFromContractedGraphs() {
        bpGraph = new BPGraph(contracted, contracted);

        assertEquals(2, bpGraph.colors.size());
        assertTrue(bpGraph.isContracted);
    }

    @Test
    void testBPGraphFromNonContractedGraphs() {
        bpGraph = new BPGraph(nonContracted, nonContracted);

        assertEquals(2, bpGraph.colors.size());
        assertFalse(bpGraph.isContracted);
    }

    @Test
    void testAddingContractedGraphToEmptyBPGraph() {
        bpGraph = new BPGraph();
        bpGraph.add(contracted);
        assertTrue(bpGraph.isContracted);
    }

    @Test
    void testAddingNonContractedGraphToEmptyBPGraph() {
        bpGraph = new BPGraph();
        bpGraph.add(nonContracted);
        assertFalse(bpGraph.isContracted);
    }

    @Test
    void testAddingMixedGraphsToBPGraphFails() {
        bpGraph = new BPGraph();
        bpGraph.add(contracted);
        assertThrows(RuntimeException.class, ()->bpGraph.add(nonContracted));
    }

}