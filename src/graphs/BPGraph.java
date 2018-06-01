package graphs;

import genome.Genome;
import genome.Grimm;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class BPGraph {
    private ArrayList<Graph> colors = new ArrayList<>();
    private boolean isContracted;
    private HashMap<String, Boolean> availableVertices = new HashMap<>();
    private int cycleNumber = 0;
    private ArrayList<Integer> cycles = new ArrayList<>();
    private int geneNumber = 0;
    private int upperBound = 0;
    private int lowerBound = 0;
    private ArrayList<String> footprint = new ArrayList<>();
    private ArrayList<String> footprintCopy = new ArrayList<>();
    private ArrayList<String> temporarySubgraphs = new ArrayList<>();
    private ArrayList<HashMap<String, String>> edgesPreShrink;
    private ArrayList<HashMap<String, Integer>> vertexRank;
    private ArrayList<HashMap<String, Integer>> cycleRank;


    public BPGraph(){
        colors = new ArrayList<>();
        edgesPreShrink = new ArrayList<>();
    }

    public BPGraph(ContractedGraph... graphs) {
        isContracted = true;
        colors.addAll(Arrays.asList(graphs));
        addInitialAvailabilities(graphs);
        getBounds();
        initArrays();
    }

    public BPGraph(NonContractedGraph... graphs) {
        isContracted = false;
        colors.addAll(Arrays.asList(graphs));
        addInitialAvailabilities(graphs);
        initArrays();
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
        geneNumber = availableVertices.keySet().size() / 2;
        getBounds();
        initArrays();
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
        if (colors.get(color).getAdjacentNodes(node).size() == 0)
            return null;

        return colors.get(color).getAdjacentNodes(node).iterator().next();
    }
    
    public int getGeneNumber() { return geneNumber; }

    public int getCycleNumber() { return cycleNumber; }

    public int getFootprintSize() { return footprint.size(); }

    public int getUpperBound() { return upperBound; }

    public int getLowerBound() { return lowerBound; }

    public int getCycle(int i) { return cycles.get(i); }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public void setCycle(int i, int cycles) {
        this.cycles.set(i, cycles);
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
        geneNumber = this.getNodes().size() / 2;
        initArrays();
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
        geneNumber = this.getNodes().size() / 2;
    }

    private void initArrays() {
        edgesPreShrink = new ArrayList<>();
        vertexRank = new ArrayList<>();
        cycleRank = new ArrayList<>();
        for (int i = 0; i < colors.size(); ++i) {
            edgesPreShrink.add(new HashMap<>());
            vertexRank.add(new HashMap<>());
            cycleRank.add(new HashMap<>());
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
                if (colors.get(color).getAdjacentNodes(left).contains(right)) {
                    ++cycleNumber;
                } else {
                    String leftAdjacency = getFirstAdjacency(left, color);
                    String rightAdjacency = getFirstAdjacency(right, color);
                    edgesPreShrink.get(color).put(left, leftAdjacency);
                    edgesPreShrink.get(color).put(right, rightAdjacency);

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

        geneNumber -= (end - start) / 2;

    }

    public void duplicatedShrink(ArrayList<String> subGraphs, int start, int end) {
        String left, right;

        for (int i = start; i < end; i += 2) {
            left = subGraphs.get(i);
            right = subGraphs.get(i + 1);
            for (Graph color : colors) {
                if (color.getAdjacentNodes(left).contains(right)) {

                } else {

                }
            }
        }
    }

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
                    String leftAdjacency = edgesPreShrink.get(color).get(left);
                    String rightAdjacency = edgesPreShrink.get(color).get(right);
                    // remove edges
                    colors.get(color).removeEdge(leftAdjacency, rightAdjacency);

                    // set new edges
                    colors.get(color).addEdge(left, leftAdjacency);
                    colors.get(color).addEdge(right, rightAdjacency);

                    edgesPreShrink.get(color).remove(left);
                    edgesPreShrink.get(color).remove(right);
                }

            }

        }
        for (i = start; i < end; ++i) {
            removeFootprint();
        }

        geneNumber += (end - start) / 2;
    }

    // TODO: I don't think I need index ints, will remove them
    private void addFootprint(String u) {
        footprint.add(u);
    }

    private void removeFootprint() {
        footprint.remove(footprint.size() - 1);
    }

    public void cleanFootprint() {
//        // copy to a temp location
//        for (int i = 0; i < this.idx_ft; i++, ft_before_idx++) {
//            this.ft_before_rename[ft_before_idx] = this.footprint[i];
//        }
//        this.idx_ft = 0;
        footprintCopy = (ArrayList<String>) footprint.clone();
        footprint = new ArrayList<>();

    }

    public void getBounds() {

        cycles = new ArrayList<>();
        for (int i = 0; i < colors.size(); ++i) {
            for (int j = i + 1; j < colors.size(); ++j) {
                cycles.add(countCycles(i, j));
            }
        }

        //TODO: Place in separate function
        int lowestIndex = 0;
        int lowestValue = cycles.get(0);
        for (int i = 0; i < cycles.size(); ++i) {
            if (cycles.get(i) <= lowestValue) {
                lowestIndex = i;
                lowestValue = cycles.get(i);
            }
        }

        if (cycles.size() == 3) {
            upperBound = cycleNumber + (int) Math.floor((3 * geneNumber + cycles.get(0) + cycles.get(1) + cycles.get(2)) / 2);
            lowerBound = cycleNumber + geneNumber + cycles.get(0) + cycles.get(1) + cycles.get(2) - cycles.get(lowestIndex);
        }
    }

    protected int countCycles(int firstColor, int secondColor) {

        String start, left, right;
        int cycles = 0;
        int cycleIndex = -1;

        HashMap<String, Boolean> unused = copyAvailability();

        for (String node : availableVertices.keySet()) {

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
        return cycles;
    }

    protected int countCyclesWithDuplications(int firstColor, int secondColor) {

        return 0;
    }

    public ArrayList<String> getFootprint() {
        return footprint;
    }

    public void cleanTemporarySubgraphs() {
        temporarySubgraphs = new ArrayList<>();
    }

    public ArrayList<String> getTempSubgraphs() {
        return temporarySubgraphs;
    }

    public int getTempSubgraphsSize() {
        return temporarySubgraphs.size();
    }

    public String getTempSubgraphVertex(int i) {
        return temporarySubgraphs.get(i);
    }

    public void addTempSubgraphVertex(String vertex) {
        temporarySubgraphs.add(vertex);
    }

    public Set<String> getAdjacenciesInColor(String u, int color) { return colors.get(color).getAdjacentNodes(u); }

    public boolean hasEdgeInColor(String u, String v, int color) { return colors.get(color).hasEdge(u, v); }

    public Graph getMedian() {
        Graph median = new ContractedGraph();

        for (int i = 0; i < footprintCopy.size(); i += 2)
            median.addEdge(footprintCopy.get(i), footprintCopy.get(i + 1));

        for (int i = 0; i < footprint.size(); i += 2)
            median.addEdge(footprint.get(i), footprint.get(i + 1));

        //TODO: There is still a thing with next_median_adj() here
        SortedSet<String> remaining = new TreeSet<>();
        for (String node : getNodes())
            if (checkAvailable(node))
                remaining.add(node);

        Iterator itr = remaining.iterator();

        while (itr.hasNext()) {
            median.addEdge((String) itr.next(), (String) itr.next());
        }

        return median;
    }

    public void getLinearBounds(String v1, String v2) {


        for (int i = 0; i < colors.size(); ++i) {
            for (int j = i + 1; j < colors.size(); ++j) {
                countLinearCycle(v1, v2, i, j);
            }
        }

        //TODO: Place in separate function
        int lowestIndex = 0;
        int lowestValue = cycles.get(0);
        for (int i = 0; i < cycles.size(); ++i) {
            if (cycles.get(i) <= lowestValue) {
                lowestIndex = i;
                lowestValue = cycles.get(i);
            }
        }

        upperBound = cycleNumber + (int) Math.floor((3 * geneNumber + cycles.get(0) + cycles.get(1) + cycles.get(2)) / 2);
        lowerBound = cycleNumber + geneNumber + cycles.get(0) + cycles.get(1) + cycles.get(2) - cycles.get(lowestIndex);
    }

    // TODO: Error in here
    private void countLinearCycle(String v1, String v2, int c1, int c2) {
        int color = c1 + c2 - 1;
        String x, y, w, z;
        int xRank, yRank, wRank, zRank;


        if(!cycleRank.get(color).get(v1).equals(cycleRank.get(color).get(v2))) {
            cycles.set(color, cycles.get(color) - 1);
        } else {
            x = getFirstAdjacency(v1, c1);
            y = getFirstAdjacency(v1, c2);
            w = getFirstAdjacency(v2, c1);
            z = getFirstAdjacency(v2, c2);
            //TODO: Refactor this to be cleaner
            if (x == null)
                x = edgesPreShrink.get(c1).get(v1);
            if (y == null)
                y = edgesPreShrink.get(c2).get(v1);
            if (w == null)
                w = edgesPreShrink.get(c1).get(v2);
            if (z == null)
                z = edgesPreShrink.get(c2).get(v2);

            xRank = vertexRank.get(color).get(x);
            yRank = vertexRank.get(color).get(y);
            wRank = vertexRank.get(color).get(w);
            zRank = vertexRank.get(color).get(z);

            if (xRank == zRank || yRank == wRank)
                return;
            else if (x.equals(v2) || y.equals(v2) || w.equals(v1) || z.equals(v1))
                return;


            String left, right;
            String start = left = x;
            do {
                right = getFirstAdjacency(left, c2);
                left = getFirstAdjacency(right, c1);
                if (left.equals(y) || left.equals(z))
                    return;

            } while (!left.equals(start));
            cycles.set(color, cycles.get(color) + 1);
        }
    }

    public void setRanks(int c1, int c2) {
        int cycles = 0;
        String start, left, right;
        int color = c1 + c2 - 1;
        int rank;

        HashMap<String, Boolean> unused = copyAvailability();

        for (String vertex : getNodes()) {
            if (!unused.get(vertex))
                continue;

            start = left = vertex;
            rank = 0;
            do {
                right = getFirstAdjacency(left, c1);
                unused.put(left, false); unused.put(right, false);
                cycleRank.get(color).put(left, cycles);
                vertexRank.get(color).put(left, rank);
                cycleRank.get(color).put(right, cycles);
                vertexRank.get(color).put(right, rank + 1);
                rank += 2;
                left = getFirstAdjacency(right, c2);
            } while (!left.equals(start));
            ++cycles;
        }
        this.cycles.set(color, cycles);
    }

    public Graph getColor(int color) { return colors.get(color); }

    //TODO: REMOVE THIS EVENTUALLY
    public void printEdges() {
        ArrayList<HashMap<String, String>> edges = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            edges.add(new HashMap<>());
        }

        for (String node : getNodes()) {
            if (!availableVertices.get(node))
                continue;

            for (int color = 0; color < 3; ++color) {
                String adjNode = getFirstAdjacency(node, color);
                if (edges.get(color).keySet().contains(adjNode))
                    continue;

                edges.get(color).put(node, adjNode);
            }
        }

        for (int color = 0; color < 3; ++color) {
            System.out.println("Color" + color);

            for (String node : edges.get(color).keySet()) {
                System.out.println(node + " - " + edges.get(color).get(node));
            }

        }


    }


}
