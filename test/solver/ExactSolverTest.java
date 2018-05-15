package solver;

import detector.Detector;
import graphs.BPGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structs.Info;
import structs.Parameters;
import structs.SearchList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ExactSolverTest {
    BPGraph graph;
    ASMSolver solver;
    Detector detector = new Detector();
    Parameters params;
    Info info;
    SearchList list;
    String endLine = System.getProperty("line.separator");

    @BeforeEach
    void setUp() {
        solver = new ExactSolver();
        params = new Parameters();
        list = new SearchList();
        info = new Info(params);

    }

    @Test
    void testCollapseOnIdenticalGenomes() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "1 2 3 @" + endLine + ">Three" + endLine + "1 2 3 @"));

        graph = new BPGraph(in);

        assertTrue(solver.collapse(graph, detector, info, list));
        //why 27...
        assertEquals(9, graph.getCycleNumber());

    }

    @Test
    void testCollapseOnGenomeWithAS2s() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 @" + endLine +
                ">Two" + endLine + "1 -2 @" + endLine + ">Three" + endLine + "1 @" + endLine + "2 @"));

        graph = new BPGraph(in);

        assertTrue(solver.collapse(graph, detector, info, list));

        assertEquals(4, graph.getCycleNumber());
    }

    @Test
    void testCollapseOnGenomeWithAS1sAnd2s() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 @" + endLine + "3 @" +
                endLine + ">Two" + endLine + "1 -2 @" + endLine + "3 @" + endLine +
                ">Three" + endLine + "1 @" + endLine + "2 @" + endLine + "3 @"));

        graph = new BPGraph(in);

        assertTrue(solver.collapse(graph, detector, info, list));

        assertEquals(7, graph.getCycleNumber());

    }

    @Test
    void testExactSolverWithIdenticalGenomes() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "1 2 3 @" + endLine + ">Three" + endLine + "1 2 3 @"));

        graph = new BPGraph(in);

        assertEquals(9, solver.solve(graph, detector, info, list));

    }

    @Test
    void testExactSolverWithAS2() {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 @" + endLine +
                ">Two" + endLine + "1 -2 @" + endLine + ">Three" + endLine + "1 @" + endLine + "2 @"));

        graph = new BPGraph(in);

        assertEquals(4, solver.solve(graph, detector, info, list));
    }

}