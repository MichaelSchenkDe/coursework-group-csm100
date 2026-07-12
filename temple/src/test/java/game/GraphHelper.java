package game;

import java.util.HashMap;
import java.util.Map;

/**
 * Reusable, hand-built cavern graphs shared across the escape-phase tests.
 * <p>
 * Rather than re-declaring nodes and edges inside every test, each scenario is
 * built once here and looked up by name. A test then places the explorer,
 * gold, exit and time budget on the shared graph it needs and asserts the
 * behaviour. This keeps the tests short and lets one graph exercise several
 * branches of the escape logic.
 * <p>
 * The {@link Node}/{@link Tile} constructors and {@link Node#addEdge} are
 * package-private, so these builders must live in the {@code game} package.
 * Only escape-phase graphs live here; the exploration-phase fixtures are owned
 * by the exploration test suite.
 * <p>
 * Adapted from the fixture on the {@code marijke_escape} branch, trimmed to the
 * escape scenarios.
 */
public class GraphHelper {

  /**
   * Simple linear graph: A - B - C - D with uniform edge weights of 1.
   * A(gold=0) - B(gold=100) - C(gold=50) - D(gold=0)
   * <p>
   * Use for: basic path finding, gold collection along a path, shortest path
   * correctness. Node IDs: 1-4.
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
   * Diamond graph where two paths exist from A to D.
   * A-B weight 1, A-C weight 3, B-D weight 1, C-D weight 1.
   * Shortest path A to D is A-B-D (cost 2), not A-C-D (cost 4).
   * B has gold=200, C has gold=10.
   * <p>
   * Use for: verifying Dijkstra prefers lower weight paths, gold ratio
   * comparison between branches, best-target branch evaluation. Node IDs: 10-13.
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
   * Time-constrained graph: the gold tile is too expensive to visit safely.
   * Start-Exit weight 2, Start-Gold weight 10, Gold-Exit weight 10.
   * Gold tile has 1000 gold but the detour costs 20 vs a direct exit cost of 2.
   * <p>
   * Use for: verifying unsafe detours are rejected, the safety check
   * (exitDistance &gt; remainingTime), fallback to the exit when no safe branch
   * exists. Node IDs: 20-22.
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
   * Graph where all gold has already been collected.
   * A(gold=0) - B(gold=0) - C(gold=0), all weights 1.
   * <p>
   * Use for: verifying target selection finds nothing when no gold exists, the
   * escape falls back directly to the exit path, and picking up gold on an
   * empty tile does nothing. Node IDs: 30-32.
   *
   * @return map of node names to Node objects
   */
  public static Map<String, Node> noGoldGraph() {
    Node a = new Node(30L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
    Node b = new Node(31L, new Tile(0, 1, 0, Tile.Type.FLOOR));
    Node c = new Node(32L, new Tile(0, 2, 0, Tile.Type.FLOOR));

    connect(a, b, 1);
    connect(b, c, 1);

    Map<String, Node> graph = new HashMap<>();
    graph.put("A", a);
    graph.put("B", b);
    graph.put("C", c);
    return graph;
  }

  /**
   * Single node graph — the explorer starts on the exit.
   * One node that is both entrance and exit, gold=0.
   * <p>
   * Use for: verifying an immediate return when already at the exit, and that
   * the next step from a node to itself is itself. Node ID: 40.
   *
   * @return map of node names to Node objects
   */
  public static Map<String, Node> singleNodeGraph() {
    Node only = new Node(40L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));

    Map<String, Node> graph = new HashMap<>();
    graph.put("Only", only);
    return graph;
  }

  /**
   * Two gold tiles at equal gold-per-step ratio but different costs.
   * Start-GoldA weight 1 (gold=100), Start-GoldB weight 2 (gold=200), both to
   * Exit weight 1. GoldA ratio = 100/1, GoldB ratio = 200/2 — equal, so a
   * tiebreaker should prefer the lower travel cost (GoldA).
   * <p>
   * Use for: testing the result tiebreaker, verifying lower travel cost wins on
   * an equal gold ratio. Node IDs: 50-53.
   *
   * @return map of node names to Node objects
   */
  public static Map<String, Node> equalRatioGraph() {
    Node start = new Node(50L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
    Node goldA = new Node(51L, new Tile(0, 1, 100, Tile.Type.FLOOR));
    Node goldB = new Node(52L, new Tile(0, 2, 200, Tile.Type.FLOOR));
    Node exit  = new Node(53L, new Tile(0, 3, 0, Tile.Type.FLOOR));

    connect(start, goldA, 1);
    connect(start, goldB, 2);
    connect(goldA, exit, 1);
    connect(goldB, exit, 1);

    Map<String, Node> graph = new HashMap<>();
    graph.put("Start", start);
    graph.put("GoldA", goldA);
    graph.put("GoldB", goldB);
    graph.put("Exit", exit);
    return graph;
  }

  /**
   * Gold sits deep in the graph, not an immediate neighbour of the start.
   * Start - Mid1 - Mid2 - GoldDeep - Exit, all weights 1, GoldDeep gold=500.
   * <p>
   * Use for: testing the depth-limited lookahead discovers deep gold, and that
   * gold beyond the immediate neighbours is still collected. Node IDs: 60-64.
   *
   * @return map of node names to Node objects
   */
  public static Map<String, Node> deepGoldGraph() {
    Node start    = new Node(60L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
    Node mid1     = new Node(61L, new Tile(0, 1, 0, Tile.Type.FLOOR));
    Node mid2     = new Node(62L, new Tile(0, 2, 0, Tile.Type.FLOOR));
    Node goldDeep = new Node(63L, new Tile(0, 3, 500, Tile.Type.FLOOR));
    Node exit     = new Node(64L, new Tile(0, 4, 0, Tile.Type.FLOOR));

    connect(start, mid1, 1);
    connect(mid1, mid2, 1);
    connect(mid2, goldDeep, 1);
    connect(goldDeep, exit, 1);

    Map<String, Node> graph = new HashMap<>();
    graph.put("Start", start);
    graph.put("Mid1", mid1);
    graph.put("Mid2", mid2);
    graph.put("GoldDeep", goldDeep);
    graph.put("Exit", exit);
    return graph;
  }

  /**
   * Graph with a node that cannot be reached from the start.
   * A - B (weight 1) form one component; Island (id 73) has no edges at all.
   * <p>
   * Use for: verifying Dijkstra reports an unreachable node as absent from the
   * distance map. Node IDs: 70, 71, 73.
   *
   * @return map of node names to Node objects
   */
  public static Map<String, Node> disconnectedGraph() {
    Node a = new Node(70L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
    Node b = new Node(71L, new Tile(0, 1, 0, Tile.Type.FLOOR));
    Node island = new Node(73L, new Tile(5, 5, 0, Tile.Type.FLOOR));

    connect(a, b, 1);

    Map<String, Node> graph = new HashMap<>();
    graph.put("A", a);
    graph.put("B", b);
    graph.put("Island", island);
    return graph;
  }

  /**
   * Connect two nodes bidirectionally with an edge of the given weight, since
   * the cavern graph is undirected.
   *
   * @param a      first node
   * @param b      second node
   * @param weight edge weight representing the time cost to traverse
   */
  public static void connect(Node a, Node b, int weight) {
    a.addEdge(new Edge(a, b, weight));
    b.addEdge(new Edge(b, a, weight));
  }
}
