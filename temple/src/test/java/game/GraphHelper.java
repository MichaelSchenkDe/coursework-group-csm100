package game;

import java.util.HashMap;
import java.util.Map;


public class GraphHelper {

  // -------------------------------------------------------------------------
  // ESCAPE PHASE GRAPHS
  // -------------------------------------------------------------------------

  /**
    * Simple linear graph: A - B - C - D with uniform edge weights of 1.
    * A(gold=0) - B(gold=100) - C(gold=50) - D(gold=0)
    * <p>
    * Use for: basic path finding, gold collection along a path,
    * PathFinder shortest path correctness.
    * Node IDs: 1-4
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
    * Use for: verifying Dijkstra prefers lower weight paths,
    * gold ratio comparison between branches,
    * SelectNextTarget branch evaluation.
    * Node IDs: 10-13
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
    * Time-constrained graph: gold tile is too expensive to visit safely.
    * Start-Exit weight 2, Start-Gold weight 10, Gold-Exit weight 10.
    * Gold tile has 1000 gold but total detour cost is 20 vs direct exit cost 2.
    * <p>
    * Use for: verifying unsafe detours are rejected,
    * TargetSearch safety check (exitDistance > remainingTime),
    * SelectNextTarget fallback to exit when no safe branch.
    * Node IDs: 20-22
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
    * A(gold=0) - B(gold=0) - C(gold=0)
    * All tiles have gold=0 from the start.
    * <p>
    * Use for: verifying SelectNextTarget returns null when no gold exists,
    * verifying EscapeSolver falls back directly to exit path,
    * pickUpGoldIfPresent does nothing on empty tiles.
    * Node IDs: 30-32
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
    * Single node graph — explorer starts at exit.
    * Only one node which is both entrance and exit, gold=0.
    * <p>
    * Use for: verifying immediate return when already at exit,
    * edge case where explore starts on Orb,
    * NextStep returns self when start equals target.
    * Node IDs: 40
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
    * Graph with multiple gold tiles at equal ratio but different costs.
    * Start connects to GoldA (weight 1, gold=100) and GoldB (weight 2, gold=200).
    * Both connect to Exit. GoldA ratio = 100/1 = 100. GoldB ratio = 200/2 = 100.
    * Equal ratio — tiebreaker should prefer lower travel cost (GoldA).
    * <p>
    * Use for: testing TargetSearchResult.isBetterThan tiebreaker logic,
    * verifying lower travel cost wins on equal gold ratio.
    * Node IDs: 50-53
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
    * Graph where gold is deep in the graph — not an immediate neighbour of start.
    * Start - Mid1 - Mid2 - GoldDeep - Exit
    * All weights 1. GoldDeep has gold=500.
    * <p>
    * Use for: testing TargetSearch depth-limited DFS discovers deep gold,
    * verifying PathFinder finds correct path to non-adjacent gold,
    * testing that gold beyond immediate neighbours is still collected.
    * Node IDs: 60-64
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

  // -------------------------------------------------------------------------
  // EXPLORE PHASE GRAPHS
  // -------------------------------------------------------------------------

  /**
    * Linear explore graph: Start - A - B - Orb
    * Distances to Orb: Start=3, A=2, B=1, Orb=0
    * All edges weight 1.
    * <p>
    * Use for: ExploreSolver basic DFS finds Orb,
    * verifying biased DFS moves toward closer nodes first.
    * Node IDs: 70-73
    *
    * @return map of node names to Node objects
    */
  public static Map<String, Node> exploreLinearGraph() {
    Node start = new Node(70L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
    Node a     = new Node(71L, new Tile(0, 1, 0, Tile.Type.FLOOR));
    Node b     = new Node(72L, new Tile(0, 2, 0, Tile.Type.FLOOR));
    Node orb   = new Node(73L, new Tile(0, 3, 0, Tile.Type.ORB));

    connect(start, a, 1);
    connect(a, b, 1);
    connect(b, orb, 1);

    Map<String, Node> graph = new HashMap<>();
    graph.put("Start", start);
    graph.put("A", a);
    graph.put("B", b);
    graph.put("Orb", orb);
    return graph;
  }

  /**
    * Branching explore graph with dead end.
    * Start connects to Near (distance=1 to Orb) and DeadEnd (distance=5).
    * Near connects to Orb.
    * <p>
    *     Start
    *    /     \
    *  Near   DeadEnd
    *   |
    *  Orb
    * <p>
    * Use for: verifying biased DFS visits Near before DeadEnd,
    * verifying backtracking works correctly when dead end reached,
    * ExploreSolver always finds Orb even with dead ends.
    * Node IDs: 80-83
    *
    * @return map of node names to Node objects
    */
  public static Map<String, Node> exploreDeadEndGraph() {
    Node start   = new Node(80L, new Tile(0, 0, 0, Tile.Type.ENTRANCE));
    Node near    = new Node(81L, new Tile(0, 1, 0, Tile.Type.FLOOR));
    Node deadEnd = new Node(82L, new Tile(0, 2, 0, Tile.Type.FLOOR));
    Node orb     = new Node(83L, new Tile(0, 3, 0, Tile.Type.ORB));

    connect(start, near, 1);
    connect(start, deadEnd, 1);
    connect(near, orb, 1);

    Map<String, Node> graph = new HashMap<>();
    graph.put("Start", start);
    graph.put("Near", near);
    graph.put("DeadEnd", deadEnd);
    graph.put("Orb", orb);
    return graph;
  }

  // -------------------------------------------------------------------------
  // UTILITY
  // -------------------------------------------------------------------------

  /**
    * Connects two nodes bidirectionally with an edge of the given weight.
    * Both directions are added since the cavern graph is undirected.
    *
    * @param a      first node
    * @param b      second node
    * @param weight edge weight representing time cost to traverse
    */
  public static void connect(Node a, Node b, int weight) {
    Edge ab = new Edge(a, b, weight);
    Edge ba = new Edge(b, a, weight);
    a.addEdge(ab);
    b.addEdge(ba);
  }
}



