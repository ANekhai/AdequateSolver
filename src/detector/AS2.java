package detector;

import graphs.BPGraph;

import java.util.HashMap;

public class AS2 extends SubDetector{
    private String fourCycle[] = new String[4];

    public boolean AS2(BPGraph graph) { // Originally called AS2_one, not sure why
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

                //TODO: Code here will need to be refactored to accept multidegree nodes too, this requires equal gene content hmph
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

    @Override
    public void clean() {
        super.clean();
        fourCycle = new String[4];
    }

}
