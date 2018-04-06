package detector;

import graphs.BPGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Detector {
    private int numDetected;
    private ArrayList<String> foundSubgraphs;
    private HashMap<String, Boolean> valid;
    private HashMap<String, Boolean> incidentVertices;
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
                            this.addVertex(node);
                            this.addVertex(adjNode);
                            valid.put(node, false);
                            valid.put(adjNode, false);
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

    private boolean AS2_one(BPGraph graph) {
        Boolean onlyTwoColors = false;
        incidentVertices = graph.copyAvailability();

        for (int color = 0; color < graph.getColorsSize(); ++color){
            valid = (HashMap<String, Boolean>) incidentVertices.clone();

            for (String u : graph.getNodesInColor(color)) {
                if (!valid.get(u)) {
                    continue;
                }

                Set<String> v = graph.getAdjacencyInColor(u, color);


                // get other colors. HERE WE DECLARE THERE ARE ONLY THREE COLORS
                int secondColor = (color + 1) % 3, thirdColor = (color + 2) % 3;


            }

        }

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
