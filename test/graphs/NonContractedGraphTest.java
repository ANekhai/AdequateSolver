package graphs;

import structs.Genome;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class NonContractedGraphTest {
    private NonContractedGraph graph;
    private Genome genome;
    private ArrayList<String> chromosome;

    @BeforeEach
    void setUp() {
        graph = new NonContractedGraph();
        genome = new Genome();
        chromosome = new ArrayList<>();
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

        assertTrue(graph.contains("1h0") && graph.contains("2t0"));
    }

    @Test
    void testDuplicatedGenes() {
        chromosome.add("1"); chromosome.add("2"); chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(8, graph.getNodes().size());
        assertEquals(4, graph.numEdges);
    }

    @Test
    void testLargeGenes() {
        chromosome.add("100"); chromosome.add("-200");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertTrue(graph.contains("100h0") && graph.contains("200t0"));
    }

    @Test
    void testGenomeWithMultipleChromosomes() {
        chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        chromosome.clear();
        chromosome.add("3"); chromosome.add("4");
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(8, graph.getNodes().size());
        assertEquals(4, graph.numEdges);
    }

    @Test
    void testTwoDuplicatedGenome() {
        chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(8, graph.getNodes().size());
        assertEquals(4, graph.numEdges);
    }

    @Test
    void testThreeDuplicatedGenome() {
        chromosome.add("1"); chromosome.add("2");
        genome.addChromosome(chromosome, true);
        genome.addChromosome(chromosome, true);
        genome.addChromosome(chromosome, true);
        graph.addGenome(genome);

        assertEquals(12, graph.getNodes().size());
        assertEquals(6, graph.numEdges);
    }

}