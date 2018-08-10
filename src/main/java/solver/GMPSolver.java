package solver;

import detector.Detector;
import graphs.BPGraph;
import scheduler.LoadBalancer;
import structs.Info;
import structs.Parameters;
import structs.SearchList;

import java.io.File;
import java.util.ArrayList;

public class GMPSolver extends ASMSolver {
    private ArrayList<String> tempSolution = null;
    private int remainingEdges = Integer.MAX_VALUE;
    private SearchList list = new SearchList();

    public int solve(BPGraph graph, Parameters params, Detector detector, Info info) {
        //set up functions
        int cycle[] = new int[3];
        info.setMaxLower(graph.getLowerBound());
        info.setMaxUpper(graph.getUpperBound());

        if (collapse(graph, detector, info)){
            return graph.getLowerBound();
        }

        list.init(info, 0);
        info.initFileCheck(graph.getUpperBound(), graph.getLowerBound());
        info.resetTotal(0); //TODO: set 0-index thread's total to 0
        detector.clean();

        //TODO: Move this somewhere else
        File folder = new File(info.getRootDirectory());
        folder.mkdir();

        //Begin branch-and-bound algorithm
        while (info.getMaxLower() != info.getMaxUpper() && !info.isFinished() ) {
            //Some parallel bookkeeping functions and load balancing functions first
            if(info.getThreadNumber() > 1) {

                if(info.getThreadTotal(0, info.getMaxUpper()) > params.getThreadNumber()
                        && !info.isParallel()) {
                    LoadBalancer.forkThreads(graph, params, info, detector, list);
                }
                // only in the finalizing step to balance stacks
                else if (info.checkRunning() < info.getThreadNumber()
                        && info.getMaxLower() == (info.getMaxUpper() - 1)) {
                    LoadBalancer.balanceStack(graph, params, info, detector, list,
                            info.getMaxUpper(), 0);
                }
//                long oe = System.currentTimeMillis();
//                info.other_time[0] += (oe - os);
            }

            // Updates after first iteration of while loop
            if (info.isStarted()){

                if (!list.get(info.getMaxUpper(), graph, info)) {
                    list.setNull(info.getMaxUpper());
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

            if (info.checkBreakNumber(0))
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
                    tempSolution = new ArrayList<>(graph.getFootprint());
                    remainingEdges = graph.getGeneNumber(); // TODO: Is this necessary?
                }

                if (graph.getLowerBound() == info.getMaxLower() && graph.getGeneNumber() < remainingEdges) {
                    tempSolution = new ArrayList<>(graph.getFootprint());
                    remainingEdges = graph.getGeneNumber();
                }


                if (graph.getUpperBound() > info.getMaxUpper()) {
                    graph.setUpperBound(info.getMaxUpper());
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

        //Fill in remaining edges in median
        detector.detectAdequateSubgraphs(graph);
        while (detector.getNumDetected() > 0) {
            int gran = detector.getDetectedSubgraphsSize() / detector.getNumDetected();
            int tempCycleNumber = graph.getCycleNumber();
            for (int i = 0; i < detector.getNumDetected(); ++i) {
                int start = i*gran;
                int end = (i+1)*gran;

                graph.shrink(detector.getSubgraphs(), start, end);
                graph.getBounds();
                if (graph.getCycleNumber() > tempCycleNumber)
                    break;
                else {
                    graph.expand(detector.getSubgraphs(), start, end);
                }

            }
            detector.clean();
            detector.detectAdequateSubgraphs(graph);
        }


        //TODO: move this somewhere else
        folder.delete();
        return info.getMaxLower();
    }

}