package graphs;

import genome.Genome;
import genome.Grimm;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class BPGraph {
    private ArrayList<Graph> colors = new ArrayList<>();
    private boolean isContracted;
    private HashMap<String, Boolean> availableVertices = new HashMap<>();
    private int cycleNumber = 0;
    private int[] cycles = new int[3];
    private int edgeNumber = 0;
    private int upperBound = 0;
    private int lowerBound = 0;
    private ArrayList<String> footprint = new ArrayList<>();
    private int footprintIndex = 0;
    private ArrayList<String> footprintCopy = new ArrayList<>();
    private int footprintCopyIndex = 0;




    public BPGraph(){
        colors = new ArrayList<>();
    }

    public BPGraph(ContractedGraph... graphs) {
        isContracted = true;
        colors.addAll(Arrays.asList(graphs));
        initializeEdgeCount();
        addInitialAvailabilities(graphs);
        getBounds();
    }

    public BPGraph(NonContractedGraph... graphs) {
        isContracted = false;
        colors.addAll(Arrays.asList(graphs));
        initializeEdgeCount();
        addInitialAvailabilities(graphs);
    }

    public BPGraph(BufferedReader in) {
        ArrayList<Genome> genomes = new ArrayList<>();
        try {
            genomes = Grimm.Reader.parseGRIMM(in);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
            isContracted = false;
            return;
        }
        Graph contracted;
        for (Genome genome : genomes) {
            contracted = new ContractedGraph(genome);
            add(contracted);
            addInitialAvailabilities(contracted);
        }
        getBounds();
    }

    public void initializeEdgeCount() {
        for (int i = 0; i < colors.size(); ++i) {
            edgeNumber += colors.get(i).getNumEdges();
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
    
    public int getEdgeNumber() { return edgeNumber; }

    public int getCycleNumber() { return cycleNumber; }

    public int getFootprintSize() { return footprint.size(); }

    public int getUpperBound() { return upperBound; }

    public int getLowerBound() { return lowerBound; }

    public int getCycle(int i) { return cycles[i]; }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public void setCycle(int i, int cycles) {
        this.cycles[i] = cycles;
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
        edgeNumber += graph.getNumEdges();

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

    public boolean isContracted() { return isContracted; }

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

    //TODO: CIRCULAR CASE, WILL NEED MODIFICATION FOR LINEAR ONE
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
                    String leftAdjacency = getFirstAdjacency(left, color);
                    String rightAdjacency = getFirstAdjacency(right, color);
                    // remove edges
                    colors.get(color).removeEdge(left, leftAdjacency);
                    colors.get(color).removeEdge(right, rightAdjacency);
                    // add new edges
                    colors.get(color).addEdge(leftAdjacency, rightAdjacency);
                }

            }

            availableVertices.put(left, false);
            availableVertices.put(right, false);
            addFootprint(left);
            addFootprint(right);

        }

        // TODO: Need to think about this for duplicated shrink
        edgeNumber -= (end - start) / 2;

    }

    public void duplicatedShrink(ArrayList<String> subGraphs, int start, int end) {
        String left, right;

        for (int i = start; i < end; i += 2) {
            left = subGraphs.get(i);
            right = subGraphs.get(i + 1);
            for (Graph color : colors) {
                if (color.getAdjacentNodes(left).contains(right)) {
                    //TODO: verify this
                    cycleNumber += color.getEdgesConnecting(left, right).size();
                } else {



                }
            }
        }
    }


    //TODO: NEEDS TO BE BROUGHT INTO CLOSER ALIGNMENT WITH SHRINK FUNCTION
    //I'm not quite sure of the significance of this?
        public void expand(ArrayList<String> subGraphs, int start, int end) {
        int i;
        String left, right;

        for (i = start; i < end; ++i) {
            availableVertices.put(subGraphs.get(i), true);
        }

        for (i = end - 1; i >= start; i -= 2) {
            left = subGraphs.get(i - 1);
            right = subGraphs.get(i);

            for (int color = 0; color < colors.size(); ++color) {
                if (colors.get(color).getAdjacentNodes(left).contains(right)) {
                    --cycleNumber;
                } else {
                    String twoDeepLeft = getFirstAdjacency(getFirstAdjacency(left, color), color);
                    String twoDeepRight = getFirstAdjacency(getFirstAdjacency(right, color), color);
                    // remove edges
                    colors.get(color).removeEdge(twoDeepLeft, getFirstAdjacency(twoDeepLeft, color));
                    colors.get(color).removeEdge(twoDeepRight, getFirstAdjacency(twoDeepRight, color));
                    // set new edges
                    colors.get(color).addEdge(twoDeepLeft, left);
                    colors.get(color).addEdge(twoDeepRight, right);
                }

            }

        }
        for (i = start; i < end; ++i) {
            removeFootprint();
        }

        edgeNumber += (end - start) / 2;
    }

    // TODO: I don't think I need index ints, will remove them
    private void addFootprint(String u) {
        footprint.add(u);
        ++footprintIndex;
    }

    private void removeFootprint() {
        --footprintIndex;
    }

    public void cleanFootprint() {
//        // copy to a temp location
//        for (int i = 0; i < this.idx_ft; i++, ft_before_idx++) {
//            this.ft_before_rename[ft_before_idx] = this.footprint[i];
//        }
//        this.idx_ft = 0;
        footprintCopy = (ArrayList<String>) footprint.clone();
        footprintCopyIndex = footprintIndex;
        footprint = new ArrayList<>();
        footprintIndex = 0;

    }

    public void getBounds() {
        int lowestIndex = -1;

        countCycles(0, 1);
        countCycles(0, 2);
        countCycles(1, 2);

        if (cycles[0] <= cycles[1] && cycles[0] <= cycles[2]) {
            lowestIndex = 0;
        } else if (cycles[1] <= cycles[0] && cycles[1] <= cycles[2]) {
            lowestIndex = 1;
        } else if (cycles[2] <= cycles[0] && cycles[2] <= cycles[1]) {
            lowestIndex = 2;
        }
        // TODO:
        upperBound = cycleNumber + (int) Math.floor(( 3 * edgeNumber + cycles[0] + cycles[1] + cycles[2]) / 2);
        lowerBound = cycleNumber + edgeNumber + cycles[0] + cycles[1] + cycles[2] - cycles[lowestIndex];
    }

    protected void countCycles(int firstColor, int secondColor) {

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
            // TODO: BUG HERE
            if (!unused.get(node))
                continue;

            start = left = node;
            //BFS for cycles
            do {
                right = getFirstAdjacency(left, firstColor);
                unused.put(left, false);
                unused.put(right, false);
                left = getFirstAdjacency(right, secondColor);
            } while (!left.equals(start));

            ++cycles;
        }
        this.cycles[cycleIndex] = cycles;
    }

    protected int countCyclesWithDuplications(int firstColor, int secondColor) {

        return 0;
    }

}
