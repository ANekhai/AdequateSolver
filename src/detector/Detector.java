package detector;

import graphs.BPGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Detector {
    private int numDetected;
    private ArrayList<String> foundSubgraphs;
    private HashMap<String, Boolean> valid;
    private HashMap<String, Boolean> incident; // stores incident vertices to detected AS
    //For AS2
    private String[] fourCycle;
    //For AS4
    private String[] triangle;
    private String[] pointingOut;
    //represent vertices of distance 1, 2, 3 away from first node
    private String[] oneDeep;
    private String[][] twoDeep;
    private String[][] threeDeep;


    public Detector(){
        numDetected = 0;
        foundSubgraphs = new ArrayList<>();
        fourCycle = new String[4];
        triangle = new String[3];
        pointingOut = new String[3];
        oneDeep = new String[3];
        twoDeep = new String[3][3];
        threeDeep = new String[3][3];

    }

    public void addVertex(String node) {
        foundSubgraphs.add(node);
    }


    //TODO: Need to figure out how to integrate the circular and linear cases eventually, as DCJStream separates them
    public void detectAdequateSubgraphs(BPGraph graph) {
        this.clean();
        if (AS1(graph)) {
            return;
        } else if (AS2(graph)) {
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

    private boolean AS2(BPGraph graph) { // Originally called AS2_one, not sure why
        Boolean onlyTwoColors = false;
        incident = graph.copyAvailability();

        for (int color = 0; color < graph.getColorsSize(); ++color){
            valid = (HashMap<String, Boolean>) incident.clone();

            for (String u : graph.getNodesInColor(color)) {
                if (!valid.get(u)) {
                    continue;
                }
                
                //TODO: Currently I assume that each gene has only one adjacency, will need to refactor this once dealing with duplicated genes
                String v = graph.getFirstAdjacency(u, color);

                if (!valid.get(v)){
                    continue;
                }
                // TODO: perhaps check if u or v have degree > 1
                updateVisitedVertices(valid, u, v);

                // get other colors. HERE WE DECLARE THERE ARE ONLY THREE COLORS
                int secondColor = (color + 1) % 3, thirdColor = (color + 2) % 3;

                //TODO: Code here will need to be refactored to accept multidegree nodes too
                String u1 = graph.getFirstAdjacency(u, secondColor);
                String u2 = graph.getFirstAdjacency(u, thirdColor);
                String v1 = graph.getFirstAdjacency(v, secondColor);
                String v2 = graph.getFirstAdjacency(v, thirdColor);

                if(graph.getFirstAdjacency(u1, secondColor).equals(v2)
                        && valid.get(u1) && valid.get(v2)){
                    // 0 1' 2, 0
                    addVertices(u, u1, v, v2);
                    updateVisitedVertices(incident, u, u1, v, v2);
                    updateVisitedVertices(valid, u, u1, v, v2);
                } else if (graph.getFirstAdjacency(u2, color).equals(v1)
                        && valid.get(u2) && valid.get(v1)) {
                    //0 2' 1, 0
                    addVertices(u, u2, v, v1);
                    updateVisitedVertices(incident, u, u2, v, v1);
                    updateVisitedVertices(valid, u, u2, v, v1);
                } else if (graph.getFirstAdjacency(u1, thirdColor).equals(v1)
                        && valid.get(u1) && valid.get(v1)) {
                    // 0 1' 1, 2
                    addVertices(u, v, u1, v1);
                    updateVisitedVertices(incident, u, v, u1, v1);
                    updateVisitedVertices(valid, u, v, u1, v1);
                } else if (graph.getFirstAdjacency(u2, secondColor).equals(v1)
                        && valid.get(u2) && valid.get(v2)) {
                    // 0 2' 2, 1
                    addVertices(u, v, u2, v2);
                    updateVisitedVertices(incident, u, v, u2, v2);
                    updateVisitedVertices(valid, u, v, u2, v2);
                } else if (graph.getFirstAdjacency(u1, color).equals(v1)
                        && (u1.equals(v2) || v1.equals(u2)) && valid.get(u1) && valid.get(v1)) {
                    // 0 1' 1, 0 u2 == v1 || v2 == u1
                    addVertices(u, u1, v, v1);
                    updateVisitedVertices(incident, u, u1, v, v1);
                    updateVisitedVertices(valid, u, u1, v, v1);
                } else if (graph.getFirstAdjacency(u2, color).equals(v2)
                        && (u1.equals(v2) || v1.equals(u2)) && valid.get(u2) && valid.get(v2)) {
                    // 0 2' 2, 0 u1 == v2 || v1 == u2
                    addVertices(u, u2, v, v2);
                    updateVisitedVertices(incident, u, u2, v, v2);
                    updateVisitedVertices(valid, u, u2, v, v2);
                } else if (graph.getFirstAdjacency(u1, color).equals(v1)
                        && graph.getFirstAdjacency(u2, color).equals(v2)
                        && !u1.equals(v2) && !u2.equals(v1) && valid.get(u1) && valid.get(u2)
                        && valid.get(v1) && valid.get(v2)) {
                    // double four 0 1 1, 0 2' 2, 0
                    addVertices(u, v, u1, v1, u2, v2);
                    updateVisitedVertices(incident, u, v, u1, v1, u2, v2);
                    updateVisitedVertices(valid, u, v, u1, v1, u2, v2);
                }else if (foundSubgraphs.size() == 0) { // This could probably just be an else as I think foundSubgraphs should be empty?
                    if (graph.getFirstAdjacency(u1, color).equals(v1)
                            && valid.get(u1) && valid.get(v1)) {
                        onlyTwoColors = true;
                        fourCycle[0] = u;
                        fourCycle[1] = v;
                        fourCycle[2] = u1;
                        fourCycle[3] = v1;                        
                    } else if (graph.getFirstAdjacency(u2, color).equals(v2)
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

        // Detect 5-3-5 type AS4
        detect535: for (String coreNode : graph.getNodes()) {
            if (!valid.get(coreNode)) {
                continue;
            }
            for (int color1 = 0; color1 < 2; ++color1) {
                //TODO: maybe change the name
                String coreAdj1 = graph.getFirstAdjacency(coreNode, color1);
                if (!valid.get(coreAdj1)) {
                    continue;
                }

                for (int color2 = color1 + 1; color2 < 3; ++color2) {
                    String coreAdj2 = graph.getFirstAdjacency(coreNode, color2);
                    if (!valid.get(coreAdj2)) {
                        continue;
                    }

                    int color3 = 3 - color1 - color2;

                    // TODO: When refactoring make a triangle detecting function separately

                    if (graph.getFirstAdjacency(coreAdj1, color3).equals(coreAdj2)) {
                        // Triangle Detected
                        triangle[color3] = coreNode;
                        triangle[color2] = coreAdj1;
                        triangle[color1] = coreAdj2;
                        updateVisitedVertices(valid, coreNode, coreAdj1, coreAdj2);

                        // Check if three points of the triangle point out
                        for (int i = 0; i < 3; ++i) {
                            pointingOut[i] = graph.getFirstAdjacency(triangle[i], i);
                            if (!valid.get(pointingOut[i])) {
                                continue detect535;
                            }
                        }

                        // Include 2-3, 2-4, 2-5, 2-6 subgraphs
                        for (int col1 = 0; col1 < 3; ++col1) {
                            int col2 = (col1 + 1) % 3, col3 = (col1 + 2) % 3;
                            String out12 = graph.getFirstAdjacency(pointingOut[col1], col2);
                            String out13 = graph.getFirstAdjacency(pointingOut[col1], col3);

                            if (!valid.get(out12) || !valid.get(out13)) {
                                continue;
                            }

                            if (graph.isConnected(out12, pointingOut[col2])
                                    && graph.isConnected(out13, pointingOut[col3])) {
                                // Found 5-3-5 subgraph
                                addVertices(pointingOut[col2], out12, pointingOut[col3], out13, triangle[col1],
                                        pointingOut[col1], triangle[col2], triangle[col2]);

                                updateVisitedVertices(valid, pointingOut[col2], out12, pointingOut[col3], out13,
                                        triangle[col1], pointingOut[col1], triangle[col2], triangle[col3]);

                                updateVisitedVertices(incident, pointingOut[col2], out12, pointingOut[col3], out13,
                                        triangle[col1], pointingOut[col1], triangle[col2], triangle[col3]);

                                continue detect535;
                            }


                        }
                        // Triangle found, but no AS4
                        continue detect535;

                    }
                }
            }
        }

        //loop to check for 3-3-3 or 3-3-other type subgraphs
        valid = (HashMap<String, Boolean>) incident.clone();

        detect333: for (String coreNode : graph.getNodes()) {
            if (!valid.get(coreNode)) {
                continue;
            }
            //TODO: this code assumes only one adjacency per node, not necessarily true in contracted bpgraph
            for (int color1 = 0; color1 < 3; ++color1) {
                oneDeep[color1] = graph.getFirstAdjacency(coreNode, color1);
                for (int color2 = 0; color2 < 3; ++color2) {
                    if (!valid.get(graph.getFirstAdjacency(graph.getFirstAdjacency(oneDeep[color1], color2 ), color2))
                            || !valid.get(graph.getFirstAdjacency(oneDeep[color1], color2))
                            || !valid.get(oneDeep[color1]) || color1 == color2) {
                        twoDeep[color1][color2] = null;
                        threeDeep[color1][color2] = null;

                    } else {
                        twoDeep[color1][color2] = graph.getFirstAdjacency(oneDeep[color1], color2);
                        threeDeep[color1][color2] = graph.getFirstAdjacency(twoDeep[color1][color2], color1);
                    }
                }
            }

            // Check for 3-3-3
            for (int color1 = 0; color1 < 3; ++color1) {
                if (threeDeep[0][color1] == null)
                    continue;

                for (int color2 = 0; color2 < 3; ++color2) {
                    if (threeDeep[1][color2] == null)
                        continue;

                    for (int color3= 0; color3 < 3; ++color3) {
                        if (threeDeep[2][color3] == null)
                            continue;
                        if (threeDeep[0][color1].equals(threeDeep[1][color2])
                                && threeDeep[0][color1].equals(threeDeep[2][color3])) {
                            boolean found = false;
                            //TODO: called co_core in original code ??? what does this mean???
                            String coCore = threeDeep[0][color1];

                            if (color1 != color2 && color1 != color3 && color2 != color3) {
                                found = true;
                            } else if (graph.isConnected(oneDeep[0], oneDeep[1])
                                    || graph.isConnected(oneDeep[0], oneDeep[2])
                                    || graph.isConnected(oneDeep[2], oneDeep[1])
                                    || graph.isConnected(twoDeep[0][color1], twoDeep[1][color2])
                                    || graph.isConnected(twoDeep[0][color1], twoDeep[2][color3])
                                    || graph.isConnected(twoDeep[2][color3], twoDeep[1][color2])) {
                                found = true;

                            }

                            if (found) {
                                addVertices(coreNode, coCore, oneDeep[0], twoDeep[0][color1], oneDeep[1],
                                        twoDeep[1][color2], oneDeep[2], twoDeep[2][color3]);

                                updateVisitedVertices(valid, coreNode, coCore, oneDeep[0], twoDeep[0][color1],
                                        oneDeep[1], twoDeep[1][color2], oneDeep[2], twoDeep[2][color3]);
                                updateVisitedVertices(incident, coreNode, coCore, oneDeep[0], twoDeep[0][color1],
                                        oneDeep[1], twoDeep[1][color2], oneDeep[2], twoDeep[2][color3]);
                                continue detect333;
                            }
                        }
                    }
                }
            }


            // Check for 3-3-other
            for (int c1 = 0; c1 < 2; ++c1) {
                for (int c2 = c1 + 1; c2 < 3; ++c2) {
                    for (int c1c = 0; c1c < 3; ++c1c) {
                        
                    }

                }

            }

        }


        if (foundSubgraphs.size() == 0) {
            return false;
        }
        this.numDetected = 1;
        return true;
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
