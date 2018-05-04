package detector;

import graphs.BPGraph;
import graphs.ContractedGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import genome.Genome;
import genome.Grimm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AS4Test {
    BPGraph graph;
    AS4 detector = new AS4();

    String endLine = System.getProperty("line.separator");

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
    void testNoAdequateSubgraphs() {
        addGraphFromGenes("1", "2", "3", "4", "5");
        addGraphFromGenes("1", "5", "3", "2", "4");
        addGraphFromGenes("1", "3", "5", "4", "2");

        detector.AS4(graph);

        assertEquals(0, detector.numDetected);

    }

//    @Test
//    void testOnlyAS1s() {
//        addGraphFromGenes("1", "2");
//        addGraphFromGenes("1", "2");
//        addGraphFromGenes("1", "2");
//
//        detector.AS4(graph);
//
//        assertEquals(0, detector.numDetected);
//
//    }
//
//    //TODO: It seems like AS4 will detect AS2s as well... hmmm...
//
////    @Test
////    void testOnlyAS2s() {
////        addGraphFromGenes("1", "2");
////        addGraphFromGenes("1", "-2");
////        addGraphFromGenes("1", "2"); // Throw away gene
////
////        detector.AS4(graph);
////
////        assertEquals(0, detector.numDetected);
////    }
//
//
//    //TODO: These should be AS4s, gotta find the bug with them
//    @Test
//    void testAS4noTriangles() {
//        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 4 3 @" +
//                endLine + ">Two" + endLine + "1 -2 -4 @" + endLine + "3 @" + endLine + ">Three" +
//                endLine + "-1 -4 -3 -2 @"));
//        graph = new BPGraph(in);
//
//       assertTrue(detector.AS4(graph));
//
//        assertEquals(1, detector.numDetected);
//        // assertEquals(8, detector.foundSubgraphs.size());
//
//    }
//
//    @Test
//    void testAS4withTriangle() {
//        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 4 3 @" +
//                endLine + ">Two" + endLine + "1 -2 @" + endLine + "3 4 @" + endLine + ">Three" +
//                endLine + "-1 -4 -3 2 @"));
//        graph = new BPGraph(in);
//
//        assertTrue(detector.AS4(graph));
//
//        assertEquals(1, detector.numDetected);
//        assertEquals(8, detector.foundSubgraphs.size());
//
//    }
//
//    @Test
//    void testPentagonalAS4() {
//        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 4 3 2 @" +
//                endLine + ">Two" + endLine + "1 -2 @" + endLine + "-4 3 @" + endLine + ">Three" +
//                endLine + "-3 1 2 4 @"));
//        graph = new BPGraph(in);
//
//        assertTrue(detector.AS4(graph));
//
//        assertEquals(1, detector.numDetected);
//        assertEquals(8, detector.foundSubgraphs.size());
//    }

}