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
}
