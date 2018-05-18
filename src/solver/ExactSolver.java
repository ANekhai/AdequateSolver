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

                // TODO: Figure out what major_tmp is
//                g.shrink(g.major_tmp, 0, g.idx_tmp);
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

//                graph.getRankCycleNumber(0, 1);
//                graph.getRankCycleNumber(0, 2);
//                graph.getRankCycleNumber(1, 2);
//                if (info.kernel == false) {
//                    int num = 0;
//                    for (int i = 0; i < g.v_num; ++i) {
//                        if (g.check[i]) {
//                            ++num;
//                        }
//                        System.out.printf("kernel size: %d\n",num);
//                        info.kernel = true;
//                    }
//                }
//                ++info.numAS;

            }

            for (int i = 0; i < detector.getNumDetected(); ++i) {
                //TODO: rename this
                int gran = detector.getDetectedSubgraphsSize() / detector.getNumDetected();
                int start = i * gran;
                int end = (i + 1) * gran;

                graph.shrink(detector.getSubgraphs(), start, end);

                if (detector.getNumDetected() > 2) { // I am fairly certain this is only for the linear case
//                    g.c[0] = cycle[0];
//                    g.c[1] = cycle[1];
//                    g.c[2] = cycle[2];
                    for (int j = 0; j < 3; ++j) {
                        graph.setCycle(j, cycle[j]);
                    }

                    // ???
                    graph.getBounds();
                    // Definitely problems here
//                    g.get_bounds_linear(ade.major[start_major],
//                            ade.major[end_major - 1]);

                } else {
                    graph.getBounds();
                }

                if (graph.getLowerBound() > info.getMaxLower()) {
                    info.setMaxLower(graph.getLowerBound());
                }
                if (graph.getUpperBound() > info.getMaxUpper()) {
                    graph.setUpperBound(info.getMaxUpper());
                }

                if (graph.getLowerBound() >= info.getMaxUpper()) {
                    info.setMaxLower(graph.getLowerBound());
                    graph = null;
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
