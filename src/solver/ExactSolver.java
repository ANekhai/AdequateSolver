package solver;

import detector.Detector;
import graphs.BPGraph;
import structs.Info;
import structs.SearchList;

import java.io.File;

public class ExactSolver extends ASMSolver {


    public int solve(BPGraph graph, Detector detector, Info info, SearchList list) {
        //set up functions
        //TODO: Move this somewhere else (perhaps parameters...)
        File folder = new File(info.getRootFolder());

        int cycle[] = new int[3];
        info.setMaxLower(graph.getLowerBound());
        info.setMaxUpper(graph.getUpperBound());


        if (collapse(graph, detector, info, list)){
            // System.out.printf("finished in collapse %d\n", graph.getLowerBound());
            return graph.getLowerBound();
        }

        detector.clean();
        //TODO: Move this elsewhere too
        folder.mkdir();

        //actual algorithm
        while (info.getMaxLower() != info.getMaxUpper() ) {

            //Some parallel bookkeeping functions and load balancing functions first
            if(info.getThreadNumber() > 1) {
                // some stuff for load balancing
            }

            // Updates after first iteration of while loop
            if (info.getStarted()){

                if (!list.get(info.getMaxUpper(), graph, info)) {
                    list.setNull(info.getMaxUpper());

                    System.gc();
                    info.decrementMaxUpper(); //Why decrementing here?
                    continue;
                }

//                ASMSolver.checkUpdate(0); // Used when multiple threads all working at once

                graph.expand(graph.getFootprint(), 0, graph.getFootprintSize());
                graph.shrink(graph.getTempSubgraphs(), 0, graph.getTempSubgraphsSize());

//                info.total[0]--;
                list.refreshAll(info.getMaxUpper(), info);
            }
            info.addIteration();
            info.markStarted();

            if (info.checkBreakNumber())
                break;

            detector.detectAdequateSubgraphs(graph);

            // used for both linear case and AS0
            if (detector.getNumDetected() > 2) {
                graph.getBounds();

                for (int i = 0; i < 3; ++i) {
                    cycle[i] = graph.getCycle(i);
                }
                //TODO: Add this
//                graph.getRankCycleNumber(0, 1);
//                graph.getRankCycleNumber(0, 2);
//                graph.getRankCycleNumber(1, 2);

                for (int i = 0; i < graph.getColorsSize(); ++i) {
                    for (int j = i + 1; j < graph.getColorsSize(); ++j) {
                        graph.setRanks(i, j);
                    }
                }

                if (info.getKernel() == false) {
                    int num = 0;
                    for (String node : graph.getNodes()) {

                        if (graph.checkAvailable(node)) {
                            ++num;
                        }

                    }
                    info.setKernel();
                    info.setKernelSize(num);
                }
                info.incrementSubgraphNumber();

            }

            for (int i = 0; i < detector.getNumDetected(); ++i) {
                //TODO: rename this
                int gran = detector.getDetectedSubgraphsSize() / detector.getNumDetected();
                int start = i * gran;
                int end = (i + 1) * gran;

                graph.shrink(detector.getSubgraphs(), start, end);

                if (detector.getNumDetected() > 2) { // I am fairly certain this is only for the linear case
                    //TODO: Fix this.

                    for (int j = 0; j < 3; ++j) {
                        graph.setCycle(j, cycle[j]);
                    }

                    // Definitely problems here
                    graph.getLinearBounds(detector.getSubgraphVertex(start), detector.getSubgraphVertex(end - 1));

                } else {
                    graph.getBounds();
                }

                if (graph.getLowerBound() > info.getMaxLower()) {
                    info.setMaxLower(graph.getLowerBound());
                }
                if (graph.getUpperBound() > info.getMaxUpper()) {
                    //TODO: This still seems suspect to me
                    graph.setUpperBound(info.getMaxUpper());
                }

                if (graph.getLowerBound() >= info.getMaxUpper()) {
                    info.setMaxLower(graph.getLowerBound());
                    info.markFinished();
                    return info.getMaxLower();
                }

                if(graph.getGeneNumber() > 0 && graph.getUpperBound() > info.getMaxLower()) {
                    list.add(graph, detector, start, end, graph.getUpperBound(), info);
                    info.incrementCount(0);
                }
                //restore parent graph
                graph.expand(detector.getSubgraphs(), start, end);

            }

        }

        folder.delete();
        return info.getMaxLower();
    }

}
