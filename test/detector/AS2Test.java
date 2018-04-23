package detector;

import graphs.BPGraph;
import graphs.ContractedGenome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structs.Genome;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class AS2Test {
    AS2 detector = new AS2();
    BPGraph graph;


    @BeforeEach
    void setUp() {
        graph = new BPGraph();
    }

    private void addGraphFromGenes(String... genes) {
        ArrayList<String> geneArray = new ArrayList<>(Arrays.asList(genes));
        Genome order = new Genome("test", true, geneArray);
        ContractedGenome genomeGraph = new ContractedGenome(order);
        graph.add(genomeGraph);
    }

    @Test
    void testTwoColorBox() {
        addGraphFromGenes("1", "2");
        addGraphFromGenes("1", "-2");
        addGraphFromGenes("3"); // Throw away gene

        detector.AS2(graph);

        assertEquals(1, detector.numDetected);

    }

    @Test
    void testThreeColorBox() {

    }

    @Test
    void testThirdColorDiagonalBox() {

    }

    @Test
    void testThirdColorCrossBox() {

    }

}