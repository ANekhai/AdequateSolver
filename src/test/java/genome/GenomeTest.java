package genome;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenomeTest {
    private Genome testGenome;


    @BeforeEach
    void setUp() {
        testGenome = new Genome("Test");

    }

    @Test
    void testAdditionOfChromosomes(){
        ArrayList<String> chromosome = new ArrayList<>();
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
        ArrayList<String> chromosome = new ArrayList<>();
        chromosome.add("1"); chromosome.add("2");
        testGenome.addChromosome(chromosome, true);
        testGenome.addChromosome(chromosome, false);
        assertEquals("1 2 @", testGenome.getChromosome(0).toString());
        assertEquals("1 2 $", testGenome.getChromosome(1).toString());
    }


}