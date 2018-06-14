package detector;

import graphs.BPGraph;

import java.util.ArrayList;
import java.util.Set;

public class AS1 extends SubDetector {
    public boolean AS1(BPGraph graph) {
        valid = graph.copyAvailability();
        AS1 : for (String node : graph.getNodes()) {
            if (!valid.get(node)) {
                continue;
            }
            //extract adjacencies from all the colors
            ArrayList<Set<String>> adjacencies = graph.getAllAdjacencies(node);


            for (int i = 0; i < graph.getColorsSize() - 1; ++i) {
                for (String adjNode : adjacencies.get(i)) {
                    for (int j = i + 1; j < graph.getColorsSize(); ++j) {
                        if (!valid.get(node))
                            continue AS1;
                        if (adjacencies.get(j).contains(adjNode)) {
                            addVertices(node, adjNode);
                            updateVisitedVertices(valid, node, adjNode);
                        }
                    }
                }
            }

        }

        if (foundSubgraphs.size() > 0) {
            numDetected = 1;
            return true;
        } else {
            return false;
        }
    }

    //TODO: THESE MAY NEED TO BE REWORKED WHEN INTRODUCING

    private boolean detectConnectedSelfLoops(BPGraph graph, ArrayList<Set<String>> adjacencies, String node, int color) {
        if (adjacencies.get(color).contains(node))
            for (int i = 0; i < graph.getColorsSize(); ++i) {
                if (i == color)
                    continue;

                for (String adjNode : adjacencies.get(i)) {
                    if (graph.getAdjacencyInColor(adjNode, color).contains(adjNode)) {
                        addVertices(node, adjNode);
                        updateVisitedVertices(valid, node, adjNode);
                        return true;
                    }
                }
            }
        return false;
    }

    private boolean detectParallelInColor(Set<Integer> edgesConnecting, String node, String adjacentNode) {
        if (edgesConnecting.size() > 1) {
            addVertices(node, adjacentNode);
            updateVisitedVertices(valid, node, adjacentNode);
            return true;
        }
        return false;
    }

    private void detectParallelBetweenColors(Set<String> adjacentSet, String node, String adjacentNode) {
        if (adjacentSet.contains(adjacentNode)) {
            addVertices(node, adjacentNode);
            updateVisitedVertices(valid, node, adjacentNode);
        }
    }

    //TODO: I expect that duplicate AS1s may be found, so should add in checks for valid vertices after each detector
    public boolean duplicateAS1(BPGraph graph) {
        valid = graph.copyAvailability();
        AS1 : for (String node : graph.getNodes()) {
            if (!valid.get(node))
                continue;
            // get adjacencies in all colors
            ArrayList<Set<String>> adjacencies = graph.getAllAdjacencies(node);

            for (int i = 0; i < graph.getColorsSize(); ++i) {
                // Detect self loops
                if(detectConnectedSelfLoops(graph, adjacencies, node, i))
                    continue AS1;

                for (String adjNode : adjacencies.get(i)) {
                    // Detect Parallel Edges between one color
                    if( detectParallelInColor(graph.getEdgesInColor(node, adjNode, i), node, adjNode))
                        continue AS1;

                    for (int j = i + 1; j < graph.getColorsSize(); ++j) {

                        if (!valid.get(node))
                            continue AS1;

                        detectParallelBetweenColors(adjacencies.get(j), node, adjNode);
                    }
                }
            }

        }

        if (foundSubgraphs.size() > 0) {
            numDetected = 1;
            return true;
        } else {
            return false;
        }

    }

}
