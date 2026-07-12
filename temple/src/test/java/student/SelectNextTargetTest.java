package student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import game.GraphHelper;
import game.Node;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SelectNextTarget}, which picks the best safe neighbour branch
 * to head for next during the escape phase.
 */
class SelectNextTargetTest {

  /** Build a selector whose safety checks use the distances to the given exit. */
  private static SelectNextTarget selectorFor(Node exit) {
    Map<Node, Integer> exitDistances = new Dijkstra().computePath(exit).getDistanceMap();
    return new SelectNextTarget(exitDistances);
  }

  /**
   * With plenty of time, the selector should steer toward the richer branch. In
   * the diamond graph both B (gold 200, cost 1) and C (gold 10, cost 3) are safe
   * from A, so the higher gold-per-step branch B must be chosen.
   */
  @Test
  void choosesRicherBranchWhenBothSafe() {
    Map<String, Node> g = GraphHelper.diamondGraph();

    Node target = selectorFor(g.get("D")).selectBestTarget(g.get("A"), 100);

    assertEquals(g.get("B"), target, "should head for the richer, cheaper branch B, not C");
  }

  /**
   * Escape safety is absolute: a gold tile must never be chosen if reaching it
   * would leave too little time to still get out. With only 3 steps of budget,
   * the 1000-gold detour (cost 10 there, 10 back to the exit) is unaffordable,
   * so the selector must fall back to the safe direct step to the exit.
   */
  @Test
  void rejectsGoldDetourThatWouldStrandTheExplorer() {
    Map<String, Node> g = GraphHelper.timeConstrainedGraph();

    Node target = selectorFor(g.get("Exit")).selectBestTarget(g.get("Start"), 3);

    assertEquals(g.get("Exit"), target, "must reject the unsafe rich detour and step to the exit");
  }
}
