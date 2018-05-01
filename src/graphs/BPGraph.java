package graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class BPGraph {
    ArrayList<Graph> colors;
    //one idea, store type of graph contained in colors array
    boolean isContracted;
    HashMap<String, Boolean> availableVertices = new HashMap<>();
    int cycleNumber = 0;
    int[] cycles = new int[3];
    int edgeNum = 0;
    int upperBound = 0;
    int lowerBound = 0;
    ArrayList<String> footprint = new ArrayList<>();




    public BPGraph(){
        colors = new ArrayList<>();
    }

    public BPGraph(ContractedGraph... graphs) {
        isContracted = true;
        colors = new ArrayList<>();
        colors.addAll(Arrays.asList(graphs));
        initializeEdgeCount();
        addInitialAvailabilities(graphs);
        getBounds();
    }

    public BPGraph(NonContractedGraph... graphs) {
        isContracted = false;
        colors = new ArrayList<>();
        colors.addAll(Arrays.asList(graphs));
        initializeEdgeCount();
        addInitialAvailabilities(graphs);
    }

    public void initializeEdgeCount() {
        for (int i = 0; i < colors.size(); ++i) {
            edgeNum += colors.get(i).getNumEdges();
        }
    }

    //Getters and Setters

    public Set<String> getNodes() { return availableVertices.keySet(); }

    public ArrayList<Set<String>> getAllAdjacencies(String node) {
        ArrayList<Set<String>> adjacencies = new ArrayList<>();
        for (Graph color : colors) {
            adjacencies.add(color.getAdjacentNodes(node));
        }
        return adjacencies;
    }

    public int getColorsSize() { return colors.size(); }

    public Set<String> getNodesInColor(int color) { return colors.get(color).getNodes(); }

    public Set<String> getAdjacencyInColor(String node, int color) { return colors.get(color).getAdjacentNodes(node); }

    public Set<Integer> getEdgesInColor(String u, String v, int color) {
        return colors.get(color).getEdgesConnecting(u, v);
    }

    public String getFirstAdjacency(String node, int color) {
        return colors.get(color).getAdjacentNodes(node).iterator().next();
    }

    public int getUpperBound() { return upperBound; }

    public int getLowerBound() { return lowerBound; }

    public int getCycle(int i) { return cycles[i]; }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    //Member functions

    public void add(Graph graph) {
        if (colors.isEmpty()){
            isContracted = graph instanceof ContractedGraph;
        } else if ((graph instanceof NonContractedGraph && isContracted) ||
                (graph instanceof ContractedGraph && !isContracted) ) {
            //perhaps modify this to throw a checked exception instead
            throw new UnsupportedOperationException("Breakpoint graph must consist of the same genome graph type");
        }
        colors.add(graph);
        addInitialAvailabilities(graph);
        edgeNum += graph.getNumEdges();

    }

    private void addInitialAvailabilities(Graph... graphs) {
        for (Graph graph : graphs) {
            addInitialAvailabilities(graph);
        }
    }

    private void addInitialAvailabilities(Graph graph) {
        for (String node : graph.getNodes()) {
            availableVertices.putIfAbsent(node, true);
        }
    }

    public boolean checkAvailable(String node){
        if (availableVertices.get(node) != null) {
            return availableVertices.get(node);
        } else {
            return false;
        }
    }

    public HashMap<String, Boolean> copyAvailability() { return (HashMap<String, Boolean>) availableVertices.clone(); }

    public void markUsedNode(String node) {
        availableVertices.put(node, false);
    }

    public void markUsedNode(String... nodes) {
        for (String node : nodes) {
            markUsedNode(node);
        }
    }


    public boolean isConnected(String u, String v) {
        if (u == null || v == null)
            return false;

        for (Graph graph : colors) {
            if (graph.contains(u) && graph.contains(v) && graph.hasEdge(u, v)) {
                return true;
            }
        }
        return false;
    }

    //TODO: EXAMINE THIS FOR THE LINEAR CASE, AS THIS IS THE CIRCULAR ONE
    public void shrink(ArrayList<String> subGraphs, int start, int end) {
        String left, right;

        for (int i = start; i < end; i += 2) {
            left = subGraphs.get(i);
            right = subGraphs.get(i + 1);

            for (int color = 0; color < colors.size(); ++color) {
                // TODO: May need to rethink this for duplicated genes
                if (colors.get(color).getAdjacentNodes(left).contains(right)) {
                    ++cycleNumber;
                } else {
                    //TODO: This code will not work with duplicated nodes. should I remove all edges in a color from a node that's being shrunk?
                    String oneDeepLeft = getFirstAdjacency(left, color);
                    String oneDeepRight = getFirstAdjacency(right, color);
                    String twoDeepLeft = getFirstAdjacency(oneDeepLeft, color);
                    String twoDeepRight = getFirstAdjacency(oneDeepRight, color);
                    // remove edges
                    colors.get(color).removeEdge(twoDeepLeft, getFirstAdjacency(twoDeepLeft, color));
                    colors.get(color).removeEdge(twoDeepRight, getFirstAdjacency(twoDeepRight, color));
                    // add new edges
                    colors.get(color).addEdge(twoDeepLeft, oneDeepRight);
                    colors.get(color).addEdge(twoDeepRight, oneDeepLeft);
                }

            }

            availableVertices.put(left, false);
            availableVertices.put(right, false);
            addFootprint(left);
            addFootprint(right);

        }

//        edgeNum -= (end - start) / 2;

    }

    private void addFootprint(String u) { footprint.add(u); }

    public void getBounds() {
        int lowestIndex = -1;

        countCycles(0, 1);
        countCycles(0, 2);
        countCycles(1, 2);

        if (cycles[0] <= cycles[1] && cycles[0] <= cycles[2]) {
            lowestIndex = 0;
        } else if (cycles[1] <= cycles[0] && cycles[1] <= cycles[2]) {
            lowestIndex = 1;
        } else if (cycles[2] <= cycles[0]) {
            lowestIndex = 2;
        }
        // TODO:
        upperBound = cycleNumber + (int) Math.floor(( 3 * edgeNum + cycles[0] + cycles[1] + cycles[2]) / 2);
        lowerBound = cycleNumber + edgeNum + cycles[0] + cycles[1] + cycles[2] - cycles[lowestIndex];
    }

    private void countCycles(int firstColor, int secondColor) {

        String start, left, right;
        int cycles = 0;
        int cycleIndex = -1;

        HashMap<String, Boolean> unused = copyAvailability();

        if (firstColor == 0 && secondColor == 1) {
            cycleIndex = 2;
        } else if (firstColor == 0 && secondColor == 2) {
            cycleIndex = 1;
        } else if (firstColor == 1 && secondColor == 2) {
            cycleIndex = 0;
        }

        for (String node : availableVertices.keySet()) {

            // TODO: Modify this to work with duplicated nodes
            if (!unused.get(node))
                continue;

            start = left = node;
            //BFS for cycles
            do {
                right = getFirstAdjacency(left, firstColor);
                unused.put(left, false);
                unused.put(right, false);
                left = getFirstAdjacency(right, secondColor);
            } while (left != start);

            ++cycles;
        }
        this.cycles[cycleIndex] = cycles;
    }

}
