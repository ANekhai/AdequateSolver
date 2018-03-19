package Structs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class GenomeImplTest {
    private Genome testGenome;
    Vector<GenomeImpl.Chromosome>[] chromosomes;


    @BeforeEach
    void setUp() {
        testGenome = new GenomeImpl("Test");

    }

    @AfterEach
    void tearDown() {
        testGenome = null;
    }

    @Test
    void testAddingChromosomes(){
        Vector<String> chromosome = new Vector<>();
        chromosome.add("1");
        testGenome.addChromosome(chromosome, true);
        chromosome.add("2");
        testGenome.addChromosome(chromosome, false);
        assertNotEquals(testGenome.getChromosome(0).getSize(), testGenome.getChromosome(1).getSize());
    }

    @Test
    void testRetreivingFromEmptyChromosome(){
        assertEquals(testGenome.getChromosome(0), null);
    }


}