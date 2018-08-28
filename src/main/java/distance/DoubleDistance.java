package distance;

import graphs.BPGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DoubleDistance {
    private int cycleSize = 0;
    private String startNode = "";
    private ArrayList<HashSet<Integer>> remainingEdges = new ArrayList<>();


    private boolean detectCycle(BPGraph graph, int c1, int c2, int currDepth, String currNode, ArrayList<String> path) {

        if (currNode.equals(startNode) && currDepth == cycleSize)
            return true;
        else if (currDepth > cycleSize)
            return false;

        for (int edge1 : graph.getColor(c1).incidentEdges(currNode)) {
            if (!remainingEdges.get(0).contains(edge1))
                continue;

            String adjNode1 = graph.getColor(c1).getAdjacentNode(currNode, edge1);

            for (int edge2 : graph.getColor(c2).incidentEdges(adjNode1)) {
                if (!remainingEdges.get(1).contains(edge2))
                    continue;

                String adjNode2 = graph.getColor(c2).getAdjacentNode(adjNode1, edge2);

                if (detectCycle(graph, c1, c2, ++currDepth, adjNode2, path)) {
                    remainingEdges.get(0).remove(edge1);
                    remainingEdges.get(1).remove(edge2);
                    path.add(adjNode2);
                    path.add(adjNode1);

                    return true;
                }
            }
        }

        return false;

    }

    public int countCycles(BPGraph graph, int c1, int c2) {
        //performs the cycle decomposition but counts cycles found.
        //TODO: Duplicate genome if genome is not duplicated

        remainingEdges.add(new HashSet<>(graph.getColor(c1).getEdges()));
        remainingEdges.add(new HashSet<>(graph.getColor(c2).getEdges()));

        //TODO: Think about how to account for shrunk nodes in BPGraph

        int cycleNumber = 0;

        for (cycleSize= 1; cycleSize <= graph.getNodeSize() ; ++cycleSize) {
            if (remainingEdges.get(0).size() == 0)
                break;

            for (String node : graph.getNodes()) {
                startNode = node;
                ArrayList<String> path = new ArrayList<>(); //TODO: convert paths found to noncontracted BPGraph
                if (detectCycle(graph,  c1, c2, 0, node, path))
                    ++cycleNumber;
            }
        }
        clean();

        return cycleNumber;

    }

    private void clean() {
        cycleSize = 0;
        startNode = "";
        remainingEdges = new ArrayList<>();
    }


}
