package detector;

import graphs.BPGraph;

import java.util.ArrayList;
import java.util.HashMap;

public class Detector {
    private int numDetected;
    private ArrayList<String> foundSubgraphs;
    private HashMap<String, Boolean> valid;
    //BUNCH OF OTHER STRUCTURES FROM DCJSTREAM DETECTOR

    public Detector(){
        numDetected = 0;
        foundSubgraphs = new ArrayList<>();

    }

    public void addVertex(String node) {
        foundSubgraphs.add(node);
    }


    //TODO: Need to figure out how to integrate the circular and linear cases eventually, as DCJStream separates them
    public void detectAdequateSubgraphs(BPGraph graph) {
        this.clean();
        if (AS1(graph)) {
            return;
        } else if (AS2_one(graph)) {
            if (numDetected > 1) {
                //transMajor(); //figure this function out
                if (AS4(graph)) {
                    return;
                } else {
                    //transMajorBack(); // and this one
                }

            }
        } else if (AS4(graph)){

        } else {
            AS0(graph);
        }
    }

    // Copied from DCJStream
    private void AS0(BPGraph graph) {
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
                addVertex(firstVertex);
                addVertex(key);
                ++numDetected;
            }

        }

    }

    private boolean AS1(BPGraph graph) {
        valid = graph.copyAvailability();
        for (String key : graph.getNodes()) {
            if (!graph.checkAvailable(key)) {
                continue;
            }
            //extract adjacencies from all the colors and use them to find subgraphs

        }

        if (foundSubgraphs.size() > 0) {
            numDetected = 1;
            return false;
        } else {
            return false;
        }
    }

    private boolean AS2_one(BPGraph graph) {
        return false;
    }

    private boolean AS4(BPGraph graph) {
        return false;
    }

    //TODO: Figure out transMajor and transMajorBack functions

    public int getNumDetected() { return numDetected; }

    public int getDetectedSubgraphsSize() { return foundSubgraphs.size(); }

    public void clean() {
        foundSubgraphs.clear();
        numDetected = 0;
        //numDetectedTemp = 0;
    }


}
