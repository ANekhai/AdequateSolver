package detector;

import com.google.common.collect.Iterables;
import graphs.BPGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Detector {
    private int numDetected;
    private ArrayList<String> foundSubgraphs;
    private ArrayList<String> foundFourCycle;
    private HashMap<String, Boolean> valid;
    private HashMap<String, Boolean> incident; // stores incident vertices to detected AS
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

    private boolean AS2_one(BPGraph graph) { // Why is it called AS2_one vs AS2_all???????
        Boolean onlyTwoColors = false;
        incident = graph.copyAvailability();

        for (int color = 0; color < graph.getColorsSize(); ++color){
            valid = (HashMap<String, Boolean>) incident.clone();

            for (String u : graph.getNodesInColor(color)) {
                if (!valid.get(u)) {
                    continue;
                }
                
                //TODO: Currently I assume that each gene has only one adjacency, will need to refactor this once dealing with duplicated genes
                Set<String> vs = graph.getAdjacencyInColor(u, color);
                String v = Iterables.getOnlyElement(vs);

                if (!valid.get(v)){
                    continue;
                }
                // TODO: perhaps check if u or v have degree > 1
                valid.put(u, false);
                valid.put(v, false);

                // get other colors. HERE WE DECLARE THERE ARE ONLY THREE COLORS
                int secondColor = (color + 1) % 3, thirdColor = (color + 2) % 3;

                //TODO: Code here will need to be refactored to accept multidegree nodes too
                String u2 = Iterables.getOnlyElement(graph.getAdjacencyInColor(u, secondColor));
                String u3 = Iterables.getOnlyElement(graph.getAdjacencyInColor(u, thirdColor));
                String v2 = Iterables.getOnlyElement(graph.getAdjacencyInColor(v, secondColor));
                String v3 = Iterables.getOnlyElement(graph.getAdjacencyInColor(v, thirdColor));

                if(Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, secondColor)).equals(v3) 
                        && valid.get(u2) && valid.get(v3)){
                    // 0 1' 2, 0
                    addVertex(u);
                    addVertex(u2);
                    addVertex(v);
                    addVertex(v3);
                    incident.put(u, false); incident.put(v, false); incident.put(u2, false); incident.put(v3, false);
                    valid.put(u, false); valid.put(v, false); valid.put(u2, false); valid.put(v3, false);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u3, color)).equals(v2)
                        && valid.get(u3) && valid.get(v2)) {
                    //0 2' 1, 0
                    addVertex(u);
                    addVertex(u3);
                    addVertex(v);
                    addVertex(v2);
                    incident.put(u, false); incident.put(v, false); incident.put(u3, false); incident.put(v2, false);
                    valid.put(u, false); valid.put(v, false);  valid.put(u3, false); valid.put(v2, false);

                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, thirdColor)).equals(v2)
                        && valid.get(u2) && valid.get(v2)) {
                    // 0 1' 1, 2
                    addVertex(u);
                    addVertex(v);
                    addVertex(u2);
                    addVertex(v2);
                    incident.put(u, false); incident.put(v, false); incident.put(u2, false); incident.put(v2, false);
                    valid.put(u, false); incident.put(v, false); incident.put(u2, false); incident.put(v2, false);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u3, secondColor)).equals(v2)
                        && valid.get(u3) && valid.get(v3)) {
                    // 0 2' 2, 1
                    addVertex(u);
                    addVertex(v);
                    addVertex(u3);
                    addVertex(v3);
                    incident.put(u, false); incident.put(v, false); incident.put(u3, false); incident.put(v3, false);
                    valid.put(u, false); incident.put(v, false); incident.put(u3, false); incident.put(v3, false);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, color)).equals(v2)
                        && (u2.equals(v3) || v2.equals(u3)) && valid.get(u2) && valid.get(v2)) {
                    // 0 1' 1, 0 u3 == v2 || v3 == u2
                    addVertex(u);
                    addVertex(u2);
                    addVertex(v);
                    addVertex(v2);
                    incident.put(u, false); incident.put(v, false); incident.put(u2, false); incident.put(v2, false);
                    valid.put(u, false); valid.put(v, false); valid.put(u2, false); valid.put(v2, false);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u3, color)).equals(v3)
                        && (u2.equals(v3) || v2.equals(u3)) && valid.get(u3) && valid.get(v3)) {
                    // 0 2' 2, 0 u2 == v3 || v2 == u3
                    addVertex(u);
                    addVertex(u3);
                    addVertex(v);
                    addVertex(v3);
                    incident.put(u, false); incident.put(v, false); incident.put(u3, false); incident.put(v3, false);
                    valid.put(u, false); valid.put(v, false); valid.put(u3, false); valid.put(v3, false);

                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, color)).equals(v2)
                        && Iterables.getOnlyElement(graph.getAdjacencyInColor(u3, color)).equals(v3)
                        && !u2.equals(v3) && !u3.equals(v2) && valid.get(u2) && valid.get(u3)
                        && valid.get(v2) && valid.get(v3)) {
                    // double four 0 1 1, 0 2' 2, 0
                    addVertex(u);
                    addVertex(v);
                    addVertex(u2);
                    addVertex(v2);
                    addVertex(u3);
                    addVertex(v3);
                    incident.put(u, false); incident.put(v, false); incident.put(u2, false);
                    incident.put(v2, false); incident.put(u3, false); incident.put(v3, false);
                    valid.put(u, false); valid.put(v, false); valid.put(u2, false);
                    valid.put(v2, false); valid.put(u3, false); valid.put(v3, false);
                }else if (foundSubgraphs.size() == 0) { // This could probably just be an else as I think foundSubgraphs should be empty?
                    foundFourCycle = new ArrayList<>();
                    if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, color)).equals(v2)
                            && valid.get(u2) && valid.get(v2)) {
                        onlyTwoColors = true;
                        foundFourCycle.add(u);
                        foundFourCycle.add(v);
                        foundFourCycle.add(u2);
                        foundFourCycle.add(v2);
                    } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u3, color)).equals(v3)
                            && valid.get(u3) && valid.get(v3)) {
                        onlyTwoColors = true;
                        foundFourCycle.add(u);
                        foundFourCycle.add(v);
                        foundFourCycle.add(u3);
                        foundFourCycle.add(v3);

                    }

                }

            }

        }

        if (foundSubgraphs.size() > 0) {
            numDetected = 1;
            return true;
        } else if (onlyTwoColors) {
            addVertex(foundFourCycle.get(0)); addVertex(foundFourCycle.get(1));
            addVertex(foundFourCycle.get(2)); addVertex(foundFourCycle.get(4));
            addVertex(foundFourCycle.get(0)); addVertex(foundFourCycle.get(2));
            addVertex(foundFourCycle.get(1)); addVertex(foundFourCycle.get(3));
            numDetected = 2;
            return true;
        } else {
            return false;
        }
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
