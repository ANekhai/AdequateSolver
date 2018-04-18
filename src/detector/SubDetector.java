package detector;

import java.util.ArrayList;
import java.util.HashMap;

public class SubDetector {
    protected int numDetected;
    protected ArrayList<String> foundSubgraphs;
    protected HashMap<String, Boolean> valid;
    protected HashMap<String, Boolean> incident;

    private void addVertex(String vertex) { foundSubgraphs.add(vertex); }

    protected void addVertices(String... vertices) {
        for (String vertex : vertices) {
            addVertex(vertex);
        }
    }

    protected void updateVisitedVertices(HashMap<String, Boolean> map, String... vertices) {
        for (String vertex : vertices) {
            map.put(vertex, false);
        }
    }



}
