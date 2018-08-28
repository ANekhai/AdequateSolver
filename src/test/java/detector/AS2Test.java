package detector;

import genome.Genome;
import graphs.BPGraph;
import graphs.ContractedGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        assertFalse(detector.AS2(graph));

        assertEquals(0, detector.numDetected);
    }

    @Test
    void testTwoColorBox() {
        addGraphFromGenes("1", "2");
        addGraphFromGenes("1", "-2");
        addGraphFromGenes("1", "2"); // Throw away gene

        assertTrue(detector.AS2(graph));

        assertEquals(1, detector.numDetected);
    }

    @Test
    void testThreeColorBox() {
        addGraphFromGenes("1", "-2");
        ContractedGraph circularGenes = getGenomeWithSingleCircularChromosomes("1", "2");
        graph.add(circularGenes); graph.add(circularGenes);

        assertTrue(detector.AS2(graph));

        assertEquals(1, detector.numDetected);

    }

    //TODO: Think about these two, as they are the same right now. hmm...

    @Test
    void testThirdColorDiagonalBox() {
        addGraphFromGenes("1", "-2");
        addGraphFromGenes("1", "2");
        ContractedGraph circularGenes = getGenomeWithSingleCircularChromosomes("1", "2");
        graph.add(circularGenes);

        assertTrue(detector.AS2(graph));

        assertEquals(1, detector.numDetected);
    }

    @Test
    void testThirdColorCrossBox() {
        addGraphFromGenes("1", "-2");
        addGraphFromGenes("1", "2");
        ContractedGraph circularGenes = getGenomeWithSingleCircularChromosomes("1", "2");
        graph.add(circularGenes);

        assertTrue(detector.AS2(graph));

        assertEquals(1, detector.numDetected);
    }

}