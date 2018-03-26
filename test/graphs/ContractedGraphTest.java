package graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structs.Genome;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class ContractedGraphTest {
    private Genome genome;
    private Vector<String> chromosome;
    private ContractedGraph graph;

    @BeforeEach
    void setUp() {
        genome = new Genome();
        chromosome = new Vector<>();
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


}