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

    //SubGraph Detectors
    //TODO: condense these into a list storing subdetectors
    AS0 as0 = new AS0();
    AS1 as1 = new AS1();
    AS2 as2 = new AS2();
    AS4 as4 = new AS4();




    public Detector(){
        numDetected = 0;
        foundSubgraphs = new ArrayList<>();
    }

    public void addVertex(String node) {
        foundSubgraphs.add(node);
    }

    public int getNumDetected() { return numDetected; }

    public int getDetectedSubgraphsSize() { return foundSubgraphs.size(); }

    public void addVertices(String... vertices) {
        for (String vertex : vertices) {
            addVertex(vertex);
        }
    }


    private void copy(SubDetector detector) {
        numDetected = detector.getNumDetected();
        foundSubgraphs = detector.getFoundSubgraphs();
        valid = detector.getValidVertices();
    }

    public void clean() {
        foundSubgraphs.clear();
        numDetected = 0;
        //numDetectedTemp = 0;
        as0.clean();
        as1.clean();
        as2.clean();
        as4.clean();
    }



    //TODO: Need to figure out how to integrate the circular and linear cases eventually, as DCJStream separates them
    public void detectAdequateSubgraphs(BPGraph graph) {
        this.clean();
        if (as1.AS1(graph)) {
            copy(as1);
        } else if (as2.AS2(graph)) {
            if (as2.getNumDetected() > 1) {
                if (as4.AS4(graph)) {
                    copy(as4);
                } else {
                    copy(as2);
                }
            }
        } else if (as4.AS4(graph)){
            copy(as4);
        } else {
            as0.AS0(graph);
            copy(as0);
        }
    }
    // have an idea on how to refactor this post testing

}
