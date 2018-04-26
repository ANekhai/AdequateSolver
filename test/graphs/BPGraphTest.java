package graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structs.Genome;
import structs.Grimm;

import java.io.BufferedReader;
import java.io.IOException;
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

    @Test
    void testConnectedEdgeChecking() throws IOException {
        bpGraph = new BPGraph();
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 @" + endLine +
                ">Two" + endLine + "-2 @" + endLine + ">Three" + endLine + "3 @"));

        ArrayList<Genome> genomes = Grimm.Reader.parseGRIMM(in);
        for (Genome genome : genomes) {
            contracted = new ContractedGraph(genome);
            bpGraph.add(contracted);
        }

        assertTrue(bpGraph.isConnected("1t", "1h") && bpGraph.isConnected("2h", "2t") &&
                bpGraph.isConnected("3h", "3t"));

        assertFalse(bpGraph.isConnected("1t", "2h"));
        assertFalse(bpGraph.isConnected("4h", "4t"));

    }

}