package wrapper;

import detector.Detector;
import genome.Genome;
import graphs.BPGraph;
import solver.ExactSolver;
import structs.Info;
import structs.Parameters;
import structs.SearchList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class TestWrapper {
    static String endLine = System.getProperty("line.separator");
    static String tab = String.format("%c", '\t');


    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Please specify a GRIMM formatted genome file");
        } else if (args.length > 1) {
            System.err.println("This software currently only handles a single GRIMM file");
        } else {

            String filename = args[0];
            BPGraph graph;

            try {
                BufferedReader in = new BufferedReader(new FileReader(filename));

                graph = new BPGraph(in);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Error parsing file");
            }

            Detector detector = new Detector();
            Parameters params = new Parameters();
            Info info = new Info(params);
            SearchList list = new SearchList();
            ExactSolver solver = new ExactSolver();

            System.out.println("Initial Bounds:" + endLine + "Lower: " + graph.getLowerBound() +
                    " Upper: " + graph.getUpperBound());

            int solution = solver.solve(graph, detector, info, list);

            System.out.println("New Bound: " + solution + " Cycles Found: " + graph.getCycleNumber());

            Genome median = graph.getMedian();

            System.out.println(median.toString());

        }
    }

}
