package detector;

import graphs.BPGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class GGAPAS2Test {
    BPGraph graph;
    BufferedReader in;
    GGAPAS2 detector = new GGAPAS2();

    String endLine = System.getProperty("line.separator");

    @BeforeEach
    void setUp() {
        graph = new BPGraph();
    }

    @AfterEach
    void tearDown() {
        detector.clean();
    }

    @Test
    void testSingleColorBox() {
        in = new BufferedReader(new StringReader(">1" + endLine + "1 2 @" + endLine + "-1 2 @"));


    }

}