package solver;

import detector.Detector;
import graphs.BPGraph;
import structs.Info;
import structs.Parameters;
import structs.SearchList;

public abstract class ASMSolver {

    public abstract int solve(BPGraph graph, Parameters params, Detector detector, Info info);

    public boolean collapse(BPGraph graph, Detector detector, Info info) {

        while (true){
            detector.detectAdequateSubgraphs(graph);
            if (detector.getNumDetected() == 1) {
                info.incrementCount(0);
                info.setRoot();
                graph.shrink(detector.getSubgraphs(), 0, detector.getDetectedSubgraphsSize());
                graph.getBounds();

//                System.out.println("Collapse Bounds: " + graph.getUpperBound());

                if (graph.getLowerBound() > info.getMaxLower())
                    info.setMaxLower(graph.getLowerBound());

                info.setMaxUpper(graph.getUpperBound());

                if (graph.getLowerBound() >= info.getMaxUpper())
                    return true;

                graph.cleanFootprint();
                detector.clean();
            } else {
                detector.clean();
                break;
            }

        }

        if (info.getMaxUpper() <= info.getMaxLower()) {
            return true;
        }

        return false;
    }

    //TODO: Come back when parallelization is being implemented
    public static void checkUpdate(int threadNumber, Info info, SearchList list) {
        if (info.getCount(threadNumber) % info.getFrequency() == 0) {
            int tmp_low = 0;
            for (int i = 0; i < info.getThreadNumber(); i++)
                if (info.getLowerBound(i) > tmp_low)
                    tmp_low = info.getLowerBound(i);
            if (tmp_low > info.getLowerBound(threadNumber)) {
                list.clean(tmp_low, info);
                info.setLowerBound(threadNumber, info.getLowerBound(threadNumber));
            }
        }
    }

}
