package distance;

import graphs.BPGraph;
import graphs.ContractedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DoubleDistance {

//    public BPGraph decomposeBPGraph(BPGraph graph) {
//
//        return null;
//    }
//
//    public BPGraph decomposeContractedGraphs(ContractedGraph g1, ContractedGraph g2) {
//        //TODO: Need a duplication function to perform WGD if g1 or g2 is not duplicated
////        if (!g1.isDuplicated()) {
////
////        } else if (!g2.isDuplicated()) {
////
////        }
//
//        ArrayList<Set<Integer>> remainingEdges = new ArrayList<>();
//
//    }


    private boolean detectCycle() {



    }

    public int countCycles(BPGraph graph, int c1, int c2) {
        //performs the cycle decomposition but counts cycles found.
        //TODO: perform WGD if one of the colors is not duplicated

        ArrayList<Set<Integer>> remainingEdges = new ArrayList<>();
        remainingEdges.add(graph.getColor(c1).getEdges());
        remainingEdges.add(graph.getColor(c2).getEdges());

        //TODO: Think about how to account for shrunk nodes in BPGraph

        int cycleNumber = 0;

        for (int cycleSize= 1; cycleSize < graph.getNodeSize() ; ++cycleSize) {
            if (remainingEdges.get(0).size() == 0)
                break;

            for (String vertex : graph.getNodes()) {

                //TODO: Detect cycles and remove edges used in these cycles



            }

        }

        return cycleNumber;

    }


}
