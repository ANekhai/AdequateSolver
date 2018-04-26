package solver;

import detector.Detector;
import graphs.BPGraph;

public class ExactSolver extends ASMSolver {


    public int solve(BPGraph graph, Detector detector) {
        //set up functions
        //TODO: Think about moving everything into an INFO struct for ease of everything... this is where maxLow
        //TODO: and maxUp are in th eoriginal code
        int cycle[] = new int[3];

        detector.clean();

        //actual algorithm
        while (maxLow != maxUp ) {

            //Some parallel bookkeeping functions and load balancing
            if (!start){

                ASMSolver.checkUpdate(0);
                //TODO: FIGURE OUT PARAMETERS FOR THESE
                graph.expand();
                graph.shrink();

                if (detector.getNumDetected() > 2) {
                    graph.getBounds();

                    for (int i = 0; i < 3; ++i) {
                        cycle[i] = graph.getCycle(i);
                    }

                    graph.getRankCycleNumber(0, 1);
                    graph.getRankCycleNumber(0, 2);
                    graph.getRankCycleNumber(1, 2);
                     if (info.kernel == false) {
                         //TODO: WHAT IS ALL THIS?
                     }

                }

                for (int i = 0; i < detector.getNumDetected(); ++i) {
                    //TODO: rename this
                    int gran = detector.getDetectedSubgraphsSize() / detector.getNumDetected();
                    


                }

            }

            //A function that sets info.start to false,

            detector.detectAdequateSubgraphs(graph);


        }
        return maxLow;
    }

}
