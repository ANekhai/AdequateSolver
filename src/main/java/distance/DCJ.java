package distance;

import graphs.Graph;

import java.util.HashMap;

public class DCJ {

    //TODO: only for circular genomes right now
    static public int getDCJDistance(Graph c1, Graph c2) {
        HashMap<String, Boolean> available = new HashMap<>();
        //Note: assumes equal gene content
        for (String node : c1.getNodes())
            available.put(node, true);

        String start, left, right;
        int cycles = 0;

        for (String node : available.keySet()) {
            if (!available.get(node))
                continue;

            start = left = node;

            do {
                right = c1.getAdjacentNodes(left).iterator().next();
                available.put(left, false); available.put(right, false);
                left = c2.getAdjacentNodes(right).iterator().next();
            } while (!left.equals(start));
            ++cycles;
        }

        return cycles;
    }

}
