package solver;

import detector.Detector;
import graphs.BPGraph;
import structs.Info;
import structs.SearchList;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GAPSolver extends ASMSolver {
    long currFile = 0;
    long newFile = 0;
    int mostCycles = 0;

    public int solve(BPGraph graph, Detector detector, Info info) {

        //TODO: MOVE THIS ELSEWHERE
        File folder = new File(info.getRootFolder());
        folder.mkdir();

//        File solution = new File("solution.gen"); // TODO: add capability to change solution file name

        collapse(graph, detector, info);
        writeSolution(graph.getTempFootprint());
        graph.cleanFootprint();

        toFile(graph.getFootprint(), info);

        while (currFile != newFile) {
            //transform graph to current consideration case
            ArrayList<String> shrunkVertices = fromFile(info);

            graph.shrink(shrunkVertices, 0, shrunkVertices.size());

            detector.detectAdequateSubgraphs(graph);

            for (int i = 0; i < detector.getNumDetected(); ++i) {
                int subgraphSize = detector.getDetectedSubgraphsSize() / detector.getNumDetected();
                int start = i * subgraphSize;
                int end = (i + 1) * subgraphSize;

                graph.shrink(detector.getSubgraphs(), start, end);

                if (graph.getGeneNumber() == 0) {
                    if (graph.getCycleNumber() > mostCycles) {
                        //write solution to solution file
                        writeSolution(graph.getFootprint());
                        mostCycles = graph.getCycleNumber();
                    }
                } else {
                    // write subgraphs to file
                    toFile(graph.getFootprint(), info);

                }

                graph.expand(detector.getSubgraphs(), start, end);

            }

            graph.expand(shrunkVertices, 0, shrunkVertices.size());
            detector.clean();

        }

        //read from solution file and reshrink graph
        System.out.println("Number of Iterations: " + currFile);
        ArrayList<String> solution = readSolution();
        graph.shrink(solution, 0, solution.size());

        folder.delete(); //TODO: MOVE THIS ELSEWHERE

        return graph.getCycleNumber();
    }

    //TODO: Move these functions into their own IO library

    private ArrayList<String> fromFile(Info info) {
        ArrayList<String> edges;

        File inFile = new File(info.getRootFolder() + System.getProperty("file.separator") + currFile + ".tmp");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            edges = new ArrayList<>();
            String line = reader.readLine();
            if (line != null)
                edges.addAll(Arrays.asList(line.split(" ")));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        ++currFile;
        inFile.delete();
        return edges;
    }

    private void toFile(ArrayList<String> vertices, Info info) {
        try {
            FileWriter out = new FileWriter(info.getRootFolder() + System.getProperty("file.separator") + newFile + ".tmp");
            for (int i = 0; i < vertices.size(); ++i) {
                if (i > 0)
                    out.write(" ");

                out.write(vertices.get(i));
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        ++newFile;
    }

    //TODO: Get rid of duplicated code and allow solution file to have different name


    private ArrayList<String> readSolution () {
        ArrayList<String> edges;

        File solution = new File("solution.gen");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(solution));
            edges = new ArrayList<String>(Arrays.asList(reader.readLine().split(" ")));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        solution.delete();

        return edges;

    }

    private void writeSolution (ArrayList<String> vertices) {
        try {
            FileWriter out = new FileWriter("solution.gen");
            for (String vertex : vertices) {
                out.write(vertex + " ");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }
}
