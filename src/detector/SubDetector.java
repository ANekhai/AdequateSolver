package detector;

import java.util.ArrayList;
import java.util.HashMap;

public class SubDetector {
    protected int numDetected;
    protected ArrayList<String> foundSubgraphs;
    protected HashMap<String, Boolean> valid ;
    protected HashMap<String, Boolean> incident; // stores incident vertices to detected AS


    public SubDetector() {
        numDetected = 0;
        foundSubgraphs = new ArrayList<>();
        valid = new HashMap<>();
        incident = new HashMap<>();

    }

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

    // getters


    public int getNumDetected() { return numDetected; }

    //TODO: Maybe return a copy of these
    public ArrayList<String> getFoundSubgraphs() { return foundSubgraphs; }

    public HashMap<String, Boolean> getValidVertices() { return valid; }

    public void clean() {
        numDetected = 0;
        foundSubgraphs.clear();
        //TODO: are these needed?
        valid.clear();
        incident.clear();
        //TODO: think about the structures specific to each subdetector

    }

}
