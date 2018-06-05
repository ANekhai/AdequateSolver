package solver;

import detector.Detector;
import graphs.BPGraph;
import structs.Info;
import structs.SearchList;

import java.io.File;
import java.util.ArrayList;

public class GAPSolver extends ASMSolver {

    public int solve(BPGraph graph, Detector detector, Info info, SearchList list) {
        long currFile, newFile;
        int mostCycles;
        currFile = newFile = mostCycles = 0;


        File solution = new File("solution.gen"); // TODO: add capability to change solution file name



        while (currFile != newFile) {
            detector.detectAdequateSubgraphs(graph);
            //Read from file


            //unshrink previous shrunk edges

            for (int i = 0; i < detector.getNumDetected(); ++i) {
                int subgraphSize = detector.getDetectedSubgraphsSize() / detector.getNumDetected();
                int start = i * subgraphSize;
                int end = (i + 1) * subgraphSize;

                graph.shrink(detector.getSubgraphs(), start, end);

                if (graph.getGeneNumber() > 0) {
                    if (graph.getCycleNumber() > mostCycles) {
                        //write solution to solution file

                    }
                } else {
                    // write subgraphs to file
                    toFile(graph.getFootprint(), info, newFile);
                }

                graph.expand(detector.getSubgraphs(), start, end);

            }

            detector.clean();

        }

        //read from solution file and reshrink graph

        return graph.getCycleNumber();
    }

    ArrayList<String> fromFile(Info info, long file) {
        ArrayList<String> edges = new ArrayList<>();

        return edges;
    }

    void toFile(ArrayList<String> edges, Info info, long file) {

    }

}
