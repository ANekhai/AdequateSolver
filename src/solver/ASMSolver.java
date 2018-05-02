package solver;

import detector.Detector;
import graphs.BPGraph;

public abstract class ASMSolver {

    public abstract int solve(BPGraph g, Detector detector);

    //TODO: perhaps move detector out of ASMSolver class...

    public boolean collapse(BPGraph graph, Detector detector) {
        int maxLower = graph.getLowerBound(), maxUpper = graph.getUpperBound();
        while (true){
            detector.detectAdequateSubgraphs(graph);
            if (detector.getNumDetected() == 1) {
                //info.count[0]++;
                //info.root = true;
                graph.shrink(detector.getSubgraphs(), 0, detector.getDetectedSubgraphsSize());
                graph.getBounds();
                if (graph.getLowerBound() > maxLower)
                    maxLower = graph.getLowerBound();
//                if(!parameter.isSim())
//                    graph.cleanFootprint();
                detector.clean();
            } else {
                detector.clean();
                break;
            }

        }
        //TODO: implement rename maybe?
//        if (info.root != false && !parameter.isSim())
//            graph.rename();
        if (maxUpper <= maxLower) {
            return true;
        }
        // list.init(info, 0)
        // info.init_f_check(graph.getUpperBound(), graph.getLowerBound());
        return false;
    }

    //TODO: come back to this when working on parallelization
    public static void checkUpdate(int threadNumber) {
//        if (info.count[th_num] % info.freq == 0) {
//            int tmp_low = 0;
//            for (int i = 0; i < info.num_threads; i++)
//                if (info.max_low[i] > tmp_low)
//                    tmp_low = info.max_low[i];
//            if (tmp_low > info.max_low[th_num]) {
//                list.clean(tmp_low, info);
//                info.max_low[th_num] = info.max_low[th_num];
//            }
//        }
    }

}
