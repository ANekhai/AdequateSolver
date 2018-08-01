package solver;

import detector.Detector;
import graphs.BPGraph;
import structs.Info;
import structs.Parameters;
import structs.SearchList;

public class ExactThread implements Runnable {
    BPGraph graph;
    Parameters params;
    Info info;
    Detector detector;
    SearchList list;
    int threadID;

    public ExactThread(BPGraph graph, Parameters params, Info info, Detector detector, SearchList list, int threadID) {
        super();
        this.graph = graph;
        this.params = params;
        this.info = info;
        this.detector = detector;
        this.list = list;
        this.threadID = threadID;
    }

    @Override
    public void run() {
        //TODO: basically copy-paste ExactSolver here...

    }

}
