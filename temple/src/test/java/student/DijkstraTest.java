package student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import game.GraphHelper;
import game.Node;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Dijkstra}, which computes the least-cost distance from a
 * source tile to every reachable tile during the escape phase.
 */
class DijkstraTest {

  /**
   * Escape edges are weighted, so the shortest distance must be the cheapest
   * total weight, not the fewest hops. In the diamond graph A reaches D via the
   * weight-2 path A-B-D, never the weight-4 path A-C-D.
   */
  @Test
  void choosesCheaperRouteByWeight() {
    Map<String, Node> g = GraphHelper.diamondGraph();

    DijkstraResult fromA = new Dijkstra().computePath(g.get("A"));

    assertEquals(2, fromA.getDistance(g.get("D")), "A to D should cost 2 via B, not 4 via C");
  }

  /**
   * A tile in a disconnected component is never settled, so it must not appear
   * in the distance map at all (getDistance returns null). The escape logic
   * treats such a missing distance as "unreachable" and skips it.
   */
  @Test
  void unreachableNodeAbsentFromDistanceMap() {
    Map<String, Node> g = GraphHelper.disconnectedGraph();

    DijkstraResult fromA = new Dijkstra().computePath(g.get("A"));

    assertEquals(1, fromA.getDistance(g.get("B")), "B is one weight-1 hop from A");
    assertNull(fromA.getDistance(g.get("Island")), "the disconnected island has no distance");
  }
}
