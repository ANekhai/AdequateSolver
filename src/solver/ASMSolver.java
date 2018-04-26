package solver;

import detector.Detector;
import graphs.BPGraph;

public abstract class ASMSolver {

    public abstract int solve(BPGraph g, Detector detector);

    //TODO: perhaps move detector out of ASMSolver class...

    public boolean collapse(BPGraph graph, Detector detector) {
        return false;
    }

    //TODO: Figure this out, used for
    public static void checkUpdate(int threadNumber) {
        ;
    }

}
