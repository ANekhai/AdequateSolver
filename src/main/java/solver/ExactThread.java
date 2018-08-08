package solver;

import detector.Detector;
import graphs.BPGraph;
import scheduler.LoadBalancer;
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
        //TODO: basically copy-paste ExactSolver here... Need to think about how solution is stored.

        int cycle[] = new int[3];

        info.setTotal(threadID, info.getThreadTotal(threadID, info.getThreadMaxUpper(threadID)));

        detector.clean();

        //Begin branch-and-bound algorithm
        while (info.getThreadMaxLower(threadID) != info.getThreadMaxUpper(threadID) && !info.isFinished() ) {
            if (info.checkRunning() < info.getThreadNumber()
                        && info.getThreadMaxLower(threadID) == (info.getThreadMaxUpper(threadID) - 1)) {
                    LoadBalancer.balanceStack(graph, params, info, detector, list,
                            info.getThreadMaxUpper(threadID), threadID);
                }
//                long oe = System.currentTimeMillis();
//                info.other_time[0] += (oe - os);
            }

            info.incrementCount(threadID);

            // Updates after first iteration of while loop
            if (info.isStarted()){

                if (!list.get(info.getThreadMaxUpper(threadID), graph, info)) {
                    list.setNull(info.getThreadMaxUpper(threadID));
                    System.gc();
                    info.decrementMaxUpper();
                    continue;
                }
//                info.checkStatus(list, 0); used to print what's happening, as well as adjusting memory usage metrics
                ASMSolver.checkUpdate(0, info, list); // Used when multiple threads all working at once

                graph.expand(graph.getFootprint(), 0, graph.getFootprintSize());
                graph.shrink(graph.getTempSubgraphs(), 0, graph.getTempSubgraphsSize());

                info.decrementTotal(0);
                list.refreshAll(info.getMaxUpper(), info);
            }
            info.addIteration();
            info.markStarted();

            if (info.checkBreakNumber())
                break;

            detector.detectAdequateSubgraphs(graph);

            // used for both linear case and BruteForce
            if (detector.getNumDetected() > 2) {
                graph.getBounds();

                for (int i = 0; i < 3; ++i) {
                    cycle[i] = graph.getCycle(i);
                }

                for (int i = 0; i < graph.getColorsSize(); ++i) {
                    for (int j = i + 1; j < graph.getColorsSize(); ++j) {
                        graph.setRanks(i, j);
                    }
                }

                if (!info.getKernel()) {
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

                if (detector.getNumDetected() > 2) {

                    for (int j = 0; j < 3; ++j) {
                        graph.setCycle(j, cycle[j]);
                    }

                    graph.getLinearBounds(detector.getSubgraphVertex(start), detector.getSubgraphVertex(end - 1));

                } else {
                    graph.getBounds();
                }


                if (graph.getLowerBound() > info.getMaxLower()) {
                    list.clean(graph.getLowerBound(), info);
                    info.setMaxLower(graph.getLowerBound());
                    tempSolution = (ArrayList<String>) graph.getFootprint().clone();
                    remainingEdges = graph.getGeneNumber();
                }
                if (graph.getLowerBound() == info.getMaxLower() && graph.getGeneNumber() < remainingEdges) {
                    tempSolution = (ArrayList<String>) graph.getFootprint().clone();
                    remainingEdges = graph.getGeneNumber();
                }


                if (graph.getUpperBound() > info.getMaxUpper()) {
                    graph.setUpperBound(info.getMaxUpper());
                }

                if (graph.getLowerBound() >= info.getMaxUpper()) {
                    info.setMaxLower(graph.getLowerBound());
                    list = null;
                    System.gc();
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
        if (tempSolution != null) {
            graph.expand(graph.getFootprint(), 0, graph.getFootprintSize());
            graph.shrink(tempSolution, 0, tempSolution.size());
            graph.getBounds();
        }
        graph.getBounds();


    }

}
