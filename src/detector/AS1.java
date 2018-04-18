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
            return false;
        } else {
            return false;
        }
    }

}
