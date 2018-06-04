package solver;

import detector.Detector;
import graphs.BPGraph;
import structs.Info;
import structs.SearchList;

public abstract class ASMSolver {

    public abstract int solve(BPGraph g, Detector detector, Info info, SearchList list);

    public boolean collapse(BPGraph graph, Detector detector, Info info, SearchList list) {

        while (true){
            detector.detectAdequateSubgraphs(graph);
            if (detector.getNumDetected() == 1) {
                info.incrementCount(0);
                info.setRoot();
                graph.shrink(detector.getSubgraphs(), 0, detector.getDetectedSubgraphsSize());
                graph.getBounds();
                System.out.println("Collapse Bounds: " + graph.getUpperBound());

                if (graph.getLowerBound() > info.getMaxLower())
                    info.setMaxLower(graph.getLowerBound());

                info.setMaxUpper(graph.getUpperBound());

                if (graph.getLowerBound() >= info.getMaxUpper())
                    return true;
                detector.clean();
            } else {
                detector.clean();
                break;
            }

        }

        if (info.getMaxUpper() <= info.getMaxLower()) {
            return true;
        }
        list.init(info, 0);
        info.initFileCheck(graph.getUpperBound(), graph.getLowerBound());
        return false;
    }

    //TODO: Come back when parallelization is being implemented
//    public static void checkUpdate(int threadNumber, Info info, SearchList list) {
//        if (info.getCount(threadNumber) % info.getFrequency() == 0) {
//            int tmp_low = 0;
//            for (int i = 0; i < info.getThreadNumber(); i++)
//                if (info.max_low[i] > tmp_low)
//                    tmp_low = info.max_low[i];
//            if (tmp_low > info.max_low[th_num]) {
//                list.clean(tmp_low, info);
//                info.max_low[th_num] = info.max_low[th_num];
//            }
//        }
//    }

}
