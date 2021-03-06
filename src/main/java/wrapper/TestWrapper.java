package wrapper;

import detector.Detector;
import distance.DCJ;
import genome.Genome;
import graphs.BPGraph;
import graphs.Graph;
import solver.ASMSolver;
import solver.GMPSolver;
import solver.GAPSolver;
import structs.Info;
import structs.Parameters;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class TestWrapper {
    static String endLine = System.getProperty("line.separator");

    private static void runExactSolver(BPGraph graph, BPGraph graphCopy) {
        Detector detector = new Detector();
        Parameters params = new Parameters();
        Info info = new Info(params);
        GMPSolver solver = new GMPSolver();

        System.out.println("Initial Bounds:" + endLine + "Lower:" + graph.getLowerBound() +
                " Upper:" + graph.getUpperBound());

        int solution = solver.solve(graph, params, detector, info);

        Graph median = graph.getMedian();
        Genome medianOrder = median.toGeneOrder();
        medianOrder.setName("Median");

        int d1, d2, d3;
        d1 = DCJ.getDCJDistance(graphCopy.getColor(0), median);
        d2 = DCJ.getDCJDistance(graphCopy.getColor(1), median);
        d3 = DCJ.getDCJDistance(graphCopy.getColor(2), median);
        int sum = d1 + d2 + d3;

        System.out.println("Reported Bound:" + solution + " Calculated Bound: " + graph.getLowerBound() + "  Cycles Found:" + sum + "  Cycle Number:" + graph.getCycleNumber());

        System.out.println(medianOrder.toString());

    }

    private static void runGAPSolver(BPGraph graph, BPGraph graphCopy) {
        Detector detector = new Detector();
        Parameters params = new Parameters();
        Info info = new Info(params);
        ASMSolver solver = new GAPSolver();

        System.out.println("Initial Bounds:" + endLine + "Lower:" + graph.getLowerBound() +
                " Upper:" + graph.getUpperBound());

        int solution = solver.solve(graph, params, detector, info);

        Graph median = graph.getMedian();
        Genome medianOrder = median.toGeneOrder();
        medianOrder.setName("Median");

        int d1, d2, d3;
        d1 = DCJ.getDCJDistance(graphCopy.getColor(0), median);
        d2 = DCJ.getDCJDistance(graphCopy.getColor(1), median);
        d3 = DCJ.getDCJDistance(graphCopy.getColor(2), median);
        int sum = d1 + d2 + d3;

        System.out.println("New Bound:" + graph.getLowerBound() + "  Cycles Found:" + sum + "  Cycle Number:" + solution);

        System.out.println(medianOrder.toString());
    }


    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Please specify a GRIMM formatted genome file");
        } else if (args.length > 1) {
            System.err.println("This software currently only handles a single GRIMM file");
        } else {

            String filename = args[0];
            BPGraph graph, graphCopy;

            try {
                BufferedReader in = new BufferedReader(new FileReader(filename));
                graph = new BPGraph(in);

                in = new BufferedReader(new FileReader(filename));
                graphCopy = new BPGraph(in);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Error parsing file");
            }

            runExactSolver(graph, graphCopy);

//            runGAPSolver(graph, graphCopy);

        }
    }

}
