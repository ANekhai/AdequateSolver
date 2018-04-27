package detector;

import graphs.BPGraph;

import java.util.ArrayList;
import java.util.Set;

public class AS1 extends SubDetector {
    public boolean AS1(BPGraph graph) {
        valid = graph.copyAvailability();
        for (String node : graph.getNodes()) {
            if (!valid.get(node)) {
                continue;
            }
            //extract adjacencies from all the colors
            ArrayList<Set<String>> adjacencies = graph.getAllAdjacencies(node);
            //maybe I should assume there are only 3 colors to cut down on for loops
            for (int i = 0; i < graph.getColorsSize() - 1; ++i) {
                for (String adjNode : adjacencies.get(i)) {
                    for (int j = i + 1; j < graph.getColorsSize(); ++j) {
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

    public boolean duplicateAS1(BPGraph graph) {
        //TODO: Availability may be refactored to deal with duplicated genes, should be fine for now though
        valid = graph.copyAvailability();
        for (String node : graph.getNodes()) {
            if (!valid.get(node))
                continue;
            // get adjacencies in all colors
            ArrayList<Set<String>> adjacencies = graph.getAllAdjacencies(node);

            for (int i = 0; i < graph.getColorsSize(); ++i) {
                    // Detect self loops
                    if(detectConnectedSelfLoops(graph, adjacencies, node, i))
                        continue;

                for (String adjNode : adjacencies.get(i)) {
                    // Detect Parallel Edges between one color
                    if( detectParallelInColor(graph.getEdgesInColor(node, adjNode, i), node, adjNode))
                        continue;

                    for (int j = i + 1; j < graph.getColorsSize(); ++j)
                        detectParallelBetweenColors(adjacencies.get(j), node, adjNode);

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
