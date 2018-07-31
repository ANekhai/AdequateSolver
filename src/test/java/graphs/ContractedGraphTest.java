package graphs;


import genome.Genome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ContractedGraphTest {
    private ContractedGraph graph;
    private Genome genome;
    private ArrayList<String> chromosome;

    @BeforeEach
    void setUp() {
        genome = new Genome();
        chromosome = new ArrayList<>();
        graph = new ContractedGraph();
    }

    @Test
    void testCircularGenome() {
        chromosome.add("1"); chromosome.add("2"); chromosome.add("3");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(3, graph.numEdges);
        assertEquals(6, graph.getNodes().size());
    }

    @Test
    void testReversedGenes() {
        chromosome.add("-1"); chromosome.add("-2");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertTrue(graph.contains("1h") && graph.contains("2t"));

    }

    @Test
    void testDuplicateGenes() {
        chromosome.add("1"); chromosome.add("2"); chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(4, graph.getNodes().size());
        assertEquals(4, graph.numEdges);
    }

    @Test
    void testLargeGenes() {
        chromosome.add("100"); chromosome.add("-200");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertTrue(graph.contains("200t"));

    }

    @Test
    void testGenomeWithSelfLoops() {
        chromosome.add("1"); chromosome.add("-1");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertTrue(graph.hasEdge("1h", "1h"));
        assertTrue(graph.hasEdge("1t", "1t"));

    }

    @Test
    void testGenomeWithMultipleChromosomes() {
        chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        chromosome.clear();
        chromosome.add("3"); chromosome.add("4");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(4, graph.numEdges);
        assertEquals(8, graph.getNodes().size());
    }

    @Test
    void testTwoDuplicatedGenome() {
        chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(4, graph.getNodes().size());
        assertEquals(4, graph.numEdges);
    }

    @Test
    void testThreeDuplicatedGenome() {
        chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        genome.addChromosome(chromosome, true);
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(4, graph.getNodes().size());
        assertEquals(6, graph.numEdges);
    }

    @Test
    void testGraphToGenomeNoDuplicates() {
        chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        chromosome = new ArrayList<>();
        chromosome.add("-3"); chromosome.add("-4");
        genome.addChromosome(chromosome, true);
        chromosome = new ArrayList<>();
        chromosome.add("-5"); chromosome.add("6"); chromosome.add("-7"); chromosome.add("8");
        genome.addChromosome(chromosome, true);

        graph.addGenome(genome);

        Genome newGenome = graph.toGeneOrder();

        assertEquals(3, (int) newGenome.getSize());
        assertEquals(8, newGenome.getGeneNumber());

    }

    @Test
    void testGraphToGenomeWithDuplicatesThrowsException() {
        chromosome.add("1"); chromosome.add("2"); chromosome.add("1");
        genome.addChromosome(chromosome, true);

        graph.addGenome(genome);

        assertThrows(RuntimeException.class, ()->graph.toGeneOrder());
    }


}