package structs;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class GrimmTest {
    private BufferedReader in;
    private ArrayList<Genome> readResult;
    private static String endLine = System.getProperty("line.separator");


    @Test
    void parseChromosomeName() throws IOException {
        in = new BufferedReader(new StringReader(">Test" + endLine + "1 2 3 @"));
        readResult = Grimm.Reader.parseGRIMM(in);

        assertEquals("Test", readResult.get(0).getName());
    }

    @Test
    void parseAllGenes() throws IOException {
        in = new BufferedReader(new StringReader(">Test" + endLine + "1 2 3 11 12 13 101 @"));
        readResult = Grimm.Reader.parseGRIMM(in);

        assertEquals(7, readResult.get(0).getChromosome(0).getSize());
    }


    @Test
    void parseCircularChromosome() throws IOException {
        in = new BufferedReader(new StringReader(">Test" + endLine + "1 2 3 @"));
        readResult = Grimm.Reader.parseGRIMM(in);

        assertTrue(readResult.get(0).getChromosome(0).isCyclical());

    }

    @Test
    void parseLinearChromosome() throws IOException {
        in = new BufferedReader(new StringReader(">Test" + endLine + "1 2 3 $"));
        readResult = Grimm.Reader.parseGRIMM(in);

        assertFalse(readResult.get(0).getChromosome(0).isCyclical());
    }

    @Test
    void parseMultipleChromosomes() throws IOException {
        in = new BufferedReader(new StringReader(">Test" + endLine + "1 2 3 $" + endLine + "4 5 6 $"));
        readResult = Grimm.Reader.parseGRIMM(in);

        assertEquals((Integer) 2, readResult.get(0).getSize());
        assertEquals(3, readResult.get(0).getChromosome(1).getSize());
    }

    @Test
    void parseMultipleGenomes() throws IOException {
        in = new BufferedReader(new StringReader(">Test1" + endLine + "1 2 3 $" + endLine + ">Test2"+ endLine + "4 5 6 $"));
        readResult = Grimm.Reader.parseGRIMM(in);

        assertEquals((Integer) 2, (Integer) readResult.size());
    }

    @Test
    void parseCommentLine() throws IOException {
        in = new BufferedReader(new StringReader(">Test1" + endLine + "#My Comment" + endLine + "1 2 3 $" + endLine + "#Another Comment"));
        readResult = Grimm.Reader.parseGRIMM(in);

        assertEquals(1, readResult.size());
        assertEquals((Integer) 3, (Integer) readResult.get(0).getChromosome(0).getSize());
    }

    @Test
    void parseInLineComment() throws IOException {
        in = new BufferedReader(new StringReader(">Test1" + endLine + "1 2 3 $ #An Inline Comment"));
        readResult = Grimm.Reader.parseGRIMM(in);

        assertEquals(3, readResult.get(0).getChromosome(0).getSize());
    }

    @Test
    void parseEmptyFile() {
        in = new BufferedReader(new StringReader(""));

        assertThrows(IOException.class, ()->Grimm.Reader.parseGRIMM(in), "No genomes found in file");
    }

    @Test
    void parseEmptyGenomeAtStart() {
        in = new BufferedReader(new StringReader(">Test1" + endLine + ">Test2" + endLine + "1 2 3 @"));
        assertThrows(IOException.class, ()->Grimm.Reader.parseGRIMM(in), "Cannot parse empty genomes");
    }

    @Test
    void parseEmptyGenomeAtEnd() {
        in = new BufferedReader(new StringReader(">Test1" + endLine + "1 2 3 @" + endLine + ">Test2"));

        assertThrows(IOException.class, ()->Grimm.Reader.parseGRIMM(in), "Cannot parse empty genomes");
    }

    @Test
    void parseMalformedChromosomeLine() {
        in = new BufferedReader(new StringReader(">Test1" + endLine + "1 2 3 4"));

        assertThrows(IOException.class, ()->Grimm.Reader.parseGRIMM(in), "Chromosomes must end in '@' or '$'");
    }

    @Test
    void parseEmptyChromosome() {
        in = new BufferedReader(new StringReader(">Test1" + endLine + "@"));

        assertThrows(IOException.class, ()->Grimm.Reader.parseGRIMM(in), "Cannot parse empty chromosomes");

    }

}