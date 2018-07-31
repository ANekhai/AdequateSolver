package detector;

import graphs.BPGraph;

public class BruteForce extends SubDetector {

    public void bruteForce(BPGraph graph) {
        //No heuristic methods right now
        String firstVertex = "";
        for (String key : graph.getNodes()) {
            //get first available vertex
            if (graph.checkAvailable(key)){
                firstVertex = key;
                break;
            }
        }

        // Add edge between first vertex and each remaining vertex
        for (String key : graph.getNodes()) {
            if (!key.equals(firstVertex) && graph.checkAvailable(key)) {
                addVertices(firstVertex, key);
                ++numDetected;
            }
        }
    }

}
