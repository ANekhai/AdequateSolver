package Structs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class GenomeImplTest {
    private Genome testGenome;


    @BeforeEach
    void setUp() {
        testGenome = new GenomeImpl("Test");

    }

    @Test
    void testAdditionOfChromosomes(){
        Vector<String> chromosome = new Vector<>();
        chromosome.add("1");
        testGenome.addChromosome(chromosome, true);
        chromosome.add("2");
        testGenome.addChromosome(chromosome, false);
        assertEquals(testGenome.getChromosome(0).getSize() + 1,
                testGenome.getChromosome(1).getSize());
    }

    @Test
    void testRetrievalFromEmptyChromosome(){
        assertThrows(IndexOutOfBoundsException.class, ()->testGenome.getChromosome(1));
    }

    @Test
    void testStringConversion(){
        Vector<String> chromosome = new Vector<>();
        chromosome.add("1"); chromosome.add("2");
        testGenome.addChromosome(chromosome, true);
        testGenome.addChromosome(chromosome, false);
        assertEquals("1 2 @", testGenome.getChromosome(0).toString());
        assertEquals("1 2 $", testGenome.getChromosome(1).toString());
    }


}