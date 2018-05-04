package solver;

import detector.Detector;
import graphs.BPGraph;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ExactSolverTest {
    BPGraph graph;
    ASMSolver solver = new ExactSolver();
    Detector detector = new Detector();
    String endLine = System.getProperty("line.separator");


    @Test
    void testCollapseOnIdenticalGenomes() throws IOException {
        BufferedReader in = new BufferedReader(new StringReader(">One" + endLine + "1 2 3 @" + endLine +
                ">Two" + endLine + "1 2 3 @" + endLine + ">Three" + endLine + "1 2 3 @"));

        graph = new BPGraph(in);

        solver.collapse(graph, detector);

        assertEquals(9, graph.getCycleNumber());

    }

}