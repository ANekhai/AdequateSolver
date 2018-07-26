package detector;

import graphs.BPGraph;

import java.util.HashMap;
import java.util.Set;

public class GGAPAS2 extends SubDetector {

    private void detectSingleColorFourCycle(BPGraph graph, String u, String v, int c1, int c2) {
        Set<String> uAdjacencies = graph.getAdjacenciesInColor(u, c1);
        uAdjacencies.remove(v);
        Set<String> vAdjacencies = graph.getAdjacencyInColor(v, c1);
        vAdjacencies.remove(u);

        for (String u1 : uAdjacencies) {
            for (String v1 : vAdjacencies) {
                if (graph.hasEdgeInColor(u1, v1, c1) && valid.get(u1) && valid.get(v1)) {
                    //Four cycle with single colored edges
                    //TODO: figure out matching for median
                    //TODO: May need to add cases for single color four cycle with second color edges along the diagonal

                    updateVisitedVertices(valid, u, v, u1, v1);
                    updateVisitedVertices(incident, u, v, u1, v1);

                } else if (graph.hasEdgeInColor(u1, v1, c2) && valid.get(u1) && valid.get(v1)) {
                    //Four cycle with one edge of second color

                    updateVisitedVertices(valid, u, v, u1, v1);
                    updateVisitedVertices(incident, u, v, u1, v1);
                }
            }
        }

    }

    private void detectTwoColorFourCycles(BPGraph graph, String u, String v, int c1, int c2) {
        Set<String> uAdjacencies = graph.getAdjacenciesInColor(u, c2);
        Set<String> vAdjacencies = graph.getAdjacencyInColor(v, c2);

        for (String u1 : uAdjacencies) {
            for (String v1 : vAdjacencies) {

                if (graph.hasEdgeInColor(u1, v1, c1) && valid.get(u1) && valid.get(v1)) {
                    // four cycle found!
                    //TODO: Decide where median edges go when we have duplicated diagonal edges, may need to separate cases based on where median edges go
                    if(graph.hasEdgeInColor(u, v1, c1) || graph.hasEdgeInColor(v, u1, c1)) {

                    } else { //TODO: decide where to put median edges


                    }
                    updateVisitedVertices(valid, u, v, u1, v1);
                    updateVisitedVertices(incident, u, v, u1, v1);

                }


            }
        }
    }

    private void detectComplexSubgraphs(BPGraph graph, String u, String v, int c1, int c2) {

        String u2 = graph.getAdjacencyInColor(u, c2).iterator().next();
        String v2 = graph.getAdjacencyInColor(v, c2).iterator().next();


        if (graph.hasEdgeInColor(u2, u2, c1) && graph.hasEdgeInColor(v2, v2, c1) && valid.get(u2) && valid.get(v2)) {
            // two self loops (like bunny ears)

            updateVisitedVertices(valid, u, v, u2, v2);
            updateVisitedVertices(incident, u, v, u2, v2);

        } else if (graph.hasEdgeInColor(u2, u2, c1) && valid.get(u2)) {
            //Todo: test this statement (if v2 ones are detected later on)

            if (graph.hasEdgeInColor(u, v2, c1) && valid.get(v2)) {
                // Multicolor Triangle with loop

                updateVisitedVertices(valid, u, v, u2, v2);
                updateVisitedVertices(incident, u, v, u2, v2);

            }

            Set<String> v1Adjacencies = graph.getAdjacencyInColor(v, c1);
            v1Adjacencies.remove(u);
            for (String v1 : v1Adjacencies) {
                if (graph.hasEdgeInColor(u, v1, c1) && valid.get(v1)) {
                    //Single Color triangle with loop

                    updateVisitedVertices(valid, u, v, u2, v1);
                    updateVisitedVertices(incident, u, v, u2, v1);

                }

            }


        }




    }

    public boolean AS2(BPGraph graph) {
        for (int color = 0; color < graph.getColorsSize(); ++color) {
            valid = (HashMap<String, Boolean>) incident.clone();

            for (String u : graph.getNodesInColor(color)) {
                if (!valid.get(u))
                    continue;

                Set<String> vNodes = graph.getAdjacenciesInColor(u, color);
                int secondColor = (color + 1) % 2; //TODO: this is currently hardcoded for GHP

                for (String v : vNodes) {
                    if(!valid.get(v))
                        continue;

                    detectSingleColorFourCycle(graph, u, v, color, secondColor);

                    detectTwoColorFourCycles(graph, u, v, color, secondColor);

                }
            }
        }

        if (foundSubgraphs.size() > 0) {
            numDetected = 1;
            return true;
//        } else if (onlyTwoColors) {
//            //TODO: figure out if this case is needed and how it works for duplicated genes
        } else
            return false;

    }

}
