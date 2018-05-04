package solver;

import detector.Detector;
import graphs.BPGraph;

public class ExactSolver extends ASMSolver {


    public int solve(BPGraph graph, Detector detector) {
        //set up functions
        //TODO: Think about moving everything into an INFO struct for ease of everything... this is where maxLow
        //TODO: and maxUp are in the original code

        int cycle[] = new int[3];
        int maxLow = graph.getLowerBound(), maxUp = graph.getUpperBound();
        boolean started = false;


        if (collapse(graph, detector)){
            // System.out.printf("finished in collapse %d\n", graph.getLowerBound());
            return graph.getLowerBound();
        }

        detector.clean(); //

        //actual algorithm
        while (maxLow != maxUp ) {

            //Some parallel bookkeeping functions and load balancing functions first

            // Updates after first iteration of while loop
            if (started){

//                if (!list.get(info.max_up[0], g, info)) {
//                    list.list[info.max_up[0]] = null;
//                    System.gc();
//                    info.max_up[0]--; //Why decrementing here what?
//                    continue;
//                }

                ASMSolver.checkUpdate(0); // Used when multiple threads all working at once

                // TODO: Figure out what major_tmp is?
//                g.expand(g.footprint, 0, g.idx_ft);
//                g.shrink(g.major_tmp, 0, g.idx_tmp);

//                info.total[0]--;
//                list.refresh_all(info.max_up[0], info);
            }
//            info.count[0]++
            started = true;

//            if (info.count[0] > info.breakNum)
//                break;

            detector.detectAdequateSubgraphs(graph);

            // Only used for linear case right now so most functionality is missing
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

                if (detector.getNumDetected() > 2) { //I think this only occurs with AS0 works
//                    g.c[0] = cycle[0];
//                    g.c[1] = cycle[1];
//                    g.c[2] = cycle[2];
                    for (int j = 0; j < 3; ++j) {
                        graph.setCycle(j, cycle[j]);
                    }
//
//                    g.get_bounds_linear(ade.major[start_major],
//                            ade.major[end_major - 1]);

                } else {
                    graph.getBounds();
                }

                if (graph.getLowerBound() > maxLow) {
                    maxLow = graph.getLowerBound();
                }
                if (graph.getUpperBound() > maxUp) {
                    graph.setUpperBound(maxUp);
                    //TODO: This seems a little strange to me
                }

            }

            if (graph.getLowerBound() >= maxUp) {
                maxLow = graph.getLowerBound();
                graph = null;
                return maxLow;
            }

        }
        return maxLow;
    }

}
