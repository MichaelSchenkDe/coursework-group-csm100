package student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import game.GraphHelper;
import game.Node;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link NextStep}, which turns a Dijkstra result into the single
 * adjacent move to make next (the engine only allows one step at a time).
 * <p>
 * NextStep walks the parent chain of the given {@link DijkstraResult}, so the
 * result must be sourced at the node you are stepping <em>from</em>: the first
 * node whose parent is {@code current} is the correct next hop toward the
 * target.
 */
class NextStepTest {

  /**
   * From A heading to D on the linear graph A-B-C-D, the next move is the
   * adjacent tile B — not a jump toward D. The Dijkstra result is sourced at the
   * current node A so the parent chain leads back through it.
   */
  @Test
  void returnsFirstAdjacentHopTowardTarget() {
    Map<String, Node> g = GraphHelper.linearGraph();
    DijkstraResult fromCurrent = new Dijkstra().computePath(g.get("A"));

    Node step = NextStep.nextStep(g.get("A"), g.get("D"), fromCurrent);

    assertEquals(g.get("B"), step, "first hop from A toward D must be the adjacent tile B");
  }
}
