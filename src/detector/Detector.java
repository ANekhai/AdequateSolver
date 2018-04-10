package detector;

import com.google.common.collect.Iterables;
import graphs.BPGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Detector {
    private int numDetected;
    private ArrayList<String> foundSubgraphs;
    private String[] fourCycle = new String[4];
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

    private void addVertices(String... vertices) {
        for (String vertex : vertices) {
            addVertex(vertex);
        }
    }

    private void updateVisitedVertices(HashMap<String, Boolean> map, String... vertices) {
        for (String vertex : vertices) {
            map.put(vertex, false);
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
                updateVisitedVertices(valid, u, v);

                // get other colors. HERE WE DECLARE THERE ARE ONLY THREE COLORS
                int secondColor = (color + 1) % 3, thirdColor = (color + 2) % 3;

                //TODO: Code here will need to be refactored to accept multidegree nodes too
                String u1 = Iterables.getOnlyElement(graph.getAdjacencyInColor(u, secondColor));
                String u2 = Iterables.getOnlyElement(graph.getAdjacencyInColor(u, thirdColor));
                String v1 = Iterables.getOnlyElement(graph.getAdjacencyInColor(v, secondColor));
                String v2 = Iterables.getOnlyElement(graph.getAdjacencyInColor(v, thirdColor));

                if(Iterables.getOnlyElement(graph.getAdjacencyInColor(u1, secondColor)).equals(v2) 
                        && valid.get(u1) && valid.get(v2)){
                    // 0 1' 2, 0
                    addVertices(u, u1, v, v2);
                    updateVisitedVertices(incident, u, u1, v, v2);
                    updateVisitedVertices(valid, u, u1, v, v2);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, color)).equals(v1)
                        && valid.get(u2) && valid.get(v1)) {
                    //0 2' 1, 0
                    addVertices(u, u2, v, v1);
                    updateVisitedVertices(incident, u, u2, v, v1);
                    updateVisitedVertices(valid, u, u2, v, v1);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u1, thirdColor)).equals(v1)
                        && valid.get(u1) && valid.get(v1)) {
                    // 0 1' 1, 2
                    addVertices(u, v, u1, v1);
                    updateVisitedVertices(incident, u, v, u1, v1);
                    updateVisitedVertices(valid, u, v, u1, v1);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, secondColor)).equals(v1)
                        && valid.get(u2) && valid.get(v2)) {
                    // 0 2' 2, 1
                    addVertices(u, v, u2, v2);
                    updateVisitedVertices(incident, u, v, u2, v2);
                    updateVisitedVertices(valid, u, v, u2, v2);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u1, color)).equals(v1)
                        && (u1.equals(v2) || v1.equals(u2)) && valid.get(u1) && valid.get(v1)) {
                    // 0 1' 1, 0 u2 == v1 || v2 == u1
                    addVertices(u, u1, v, v1);
                    updateVisitedVertices(incident, u, u1, v, v1);
                    updateVisitedVertices(valid, u, u1, v, v1);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, color)).equals(v2)
                        && (u1.equals(v2) || v1.equals(u2)) && valid.get(u2) && valid.get(v2)) {
                    // 0 2' 2, 0 u1 == v2 || v1 == u2
                    addVertices(u, u2, v, v2);
                    updateVisitedVertices(incident, u, u2, v, v2);
                    updateVisitedVertices(valid, u, u2, v, v2);
                } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u1, color)).equals(v1)
                        && Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, color)).equals(v2)
                        && !u1.equals(v2) && !u2.equals(v1) && valid.get(u1) && valid.get(u2)
                        && valid.get(v1) && valid.get(v2)) {
                    // double four 0 1 1, 0 2' 2, 0
                    addVertices(u, v, u1, v1, u2, v2);
                    updateVisitedVertices(incident, u, v, u1, v1, u2, v2);
                    updateVisitedVertices(valid, u, v, u1, v1, u2, v2);
                }else if (foundSubgraphs.size() == 0) { // This could probably just be an else as I think foundSubgraphs should be empty?
                    if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u1, color)).equals(v1)
                            && valid.get(u1) && valid.get(v1)) {
                        onlyTwoColors = true;
                        fourCycle[0] = u;
                        fourCycle[1] = v;
                        fourCycle[2] = u1;
                        fourCycle[3] = v1;                        
                    } else if (Iterables.getOnlyElement(graph.getAdjacencyInColor(u2, color)).equals(v2)
                            && valid.get(u2) && valid.get(v2)) {
                        onlyTwoColors = true;
                        fourCycle[0] = u;
                        fourCycle[1] = v;
                        fourCycle[2] = u2;
                        fourCycle[3] = v2;
                    }
                }
            }
        }

        if (foundSubgraphs.size() > 0) {
            numDetected = 1;
            return true;
        } else if (onlyTwoColors) {
            addVertices(fourCycle);
            addVertices(fourCycle[0], fourCycle[2], fourCycle[1], fourCycle[3]);
            numDetected = 2;
            return true;
        } else {
            return false;
        }
    }

    private boolean AS4(BPGraph graph) {
        incident = graph.copyAvailability();
        valid = (HashMap<String, Boolean>) incident.clone();

        // Detect 5-3-5 subgraph


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
