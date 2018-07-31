package distance;

import graphs.BPGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoubleDistanceTest {
    BPGraph graph;
    DoubleDistance distance = new DoubleDistance();
    BufferedReader in;

    private String endLine = System.getProperty("line.separator");

    @AfterEach
    void tearDown() {
        graph = null;
    }

    @Test
    void testIdenticalGenomes() {
        in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 @" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "1 2 3 @" + endLine + "1 2 3 @"));
        graph = new BPGraph(in);

        assertEquals(6, distance.countCycles(graph, 0, 1));

    }

    @Test
    void testNonIdenticalGenomes() {
        in = new BufferedReader(new StringReader(">One" + endLine + "1 2 1 2 @" + endLine +
                ">Two" + endLine + "1 -2 1 2 @"));
        graph = new BPGraph(in);

        assertEquals(3, distance.countCycles(graph, 0, 1));
    }

    @Test
    void testWithSelfLoops() {
        in = new BufferedReader(new StringReader(">One" + endLine + "1 1 @" + endLine +
                ">Two" + endLine + "1 -1 @"));

        graph = new BPGraph(in);

        assertEquals(1, distance.countCycles(graph, 0, 1));
    }

    @Test
    void testLargerExample() {
        in = new BufferedReader(new StringReader(">One" + endLine + "1 -2 3 @" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "2 -3 1 -1 3 2 @"));

        graph = new BPGraph(in);

        assertEquals(3, distance.countCycles(graph, 0, 1));


    }

}