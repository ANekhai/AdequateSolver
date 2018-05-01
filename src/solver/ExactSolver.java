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

        //There is something about a collapse function here?
//        if (collapse(g, p, info, ade, list)){
//            System.out.printf("finished in collpase %d\n", g.lower_bound);
//            return g.lower_bound;
//        }

        detector.clean();

        //actual algorithm
        while (maxLow != maxUp ) {

            //Some parallel bookkeeping functions and load balancing functions first

            // Updates after first iteration of while loop
            if (started){

                //perhaps this is for load balancing
//                if (!list.get(info.max_up[0], g, info)) {
//                    list.list[info.max_up[0]] = null;
//                    System.gc();
//                    info.max_up[0]--; //Why decrementing here what?
//                    continue;
//                }

                ASMSolver.checkUpdate(0); //TODO: Implement this
//                graph.expand();
//                graph.shrink();
                // TODO: Figure out the rest of the functions in this conditional
//                g.expand(g.footprint, 0, g.idx_ft);
//                g.shrink(g.major_tmp, 0, g.idx_tmp);
//                //if(info.count[0]==8088){
//                //	System.out.println(info.count[0]);
//                //	break;
//                //}
//                info.total[0]--;
            }
//            info.count[0]++
            started = true;

//            if (info.count[0] > info.breakNum)
//                break;

            detector.detectAdequateSubgraphs(graph);

            // TODO: IT SEEMS LIKE THIS IS USED FOR THE LINEAR CASE AND NOT THE CIRCULAR CASE
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

                if (detector.getNumDetected() > 2) { //I think this is the case when AS0 detector happens
//                    g.c[0] = cycle[0];
//                    g.c[1] = cycle[1];
//                    g.c[2] = cycle[2];
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
