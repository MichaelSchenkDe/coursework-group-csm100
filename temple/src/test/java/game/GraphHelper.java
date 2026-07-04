package game;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for creating small fixed graphs for unit testing.
 * Must live in the game package to access package-private Node and Edge constructors.
 * Provides several reusable graph structures used across PathFinder and EscapeSolver tests.
 */
public class GraphHelper {

    /**
     * Creates a simple linear graph: A - B - C - D with uniform edge weights of 1.
     * A(gold=0) - B(gold=100) - C(gold=50) - D(gold=0)
     *
     * @return map of node names to Node objects
     */
    public static Map<String, Node> linearGraph() {
        Node a = new Node(1L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
        Node b = new Node(2L, new Tile(0, 1, 100, Tile.Type.FLOOR));
        Node c = new Node(3L, new Tile(0, 2, 50, Tile.Type.FLOOR));
        Node d = new Node(4L, new Tile(0, 3, 0, Tile.Type.FLOOR));

        connect(a, b, 1);
        connect(b, c, 1);
        connect(c, d, 1);

        Map<String, Node> graph = new HashMap<>();
        graph.put("A", a);
        graph.put("B", b);
        graph.put("C", c);
        graph.put("D", d);
        return graph;
    }

    /**
     * Creates a diamond graph where two paths exist from A to D.
     * A-B weight 1, A-C weight 3, B-D weight 1, C-D weight 1.
     * Shortest path A to D is A-B-D (cost 2), not A-C-D (cost 4).
     * B has gold=200, C has gold=10.
     *
     * @return map of node names to Node objects
     */
    public static Map<String, Node> diamondGraph() {
        Node a = new Node(10L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
        Node b = new Node(11L, new Tile(0, 1, 200, Tile.Type.FLOOR));
        Node c = new Node(12L, new Tile(0, 2, 10, Tile.Type.FLOOR));
        Node d = new Node(13L, new Tile(1, 1, 0, Tile.Type.FLOOR));

        connect(a, b, 1);
        connect(a, c, 3);
        connect(b, d, 1);
        connect(c, d, 1);

        Map<String, Node> graph = new HashMap<>();
        graph.put("A", a);
        graph.put("B", b);
        graph.put("C", c);
        graph.put("D", d);
        return graph;
    }

    /**
     * Creates a graph where the gold tile is expensive to reach.
     * Start-Exit weight 2, Start-Gold weight 10, Gold-Exit weight 10.
     * Gold tile has 1000 gold but total detour cost is 20 vs direct exit cost 2.
     *
     * @return map of node names to Node objects
     */
    public static Map<String, Node> timeConstrainedGraph() {
        Node start = new Node(20L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
        Node exit  = new Node(21L, new Tile(0, 1, 0, Tile.Type.FLOOR));
        Node gold  = new Node(22L, new Tile(0, 2, 1000, Tile.Type.FLOOR));

        connect(start, exit, 2);
        connect(start, gold, 10);
        connect(gold, exit, 10);

        Map<String, Node> graph = new HashMap<>();
        graph.put("Start", start);
        graph.put("Exit", exit);
        graph.put("Gold", gold);
        return graph;
    }

    /**
     * Connects two nodes bidirectionally with an edge of the given weight.
     *
     * @param a      first node
     * @param b      second node
     * @param weight edge weight (time cost)
     */
    public static void connect(Node a, Node b, int weight) {
        Edge ab = new Edge(a, b, weight);
        Edge ba = new Edge(b, a, weight);
        a.addEdge(ab);
        b.addEdge(ba);
    }
}
