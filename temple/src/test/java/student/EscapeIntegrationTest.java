package student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import game.GraphHelper;
import game.Node;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests for {@link Explorer#escape}, driving the whole escape stack
 * (Dijkstra, SelectNextTarget, TargetSearch, NextStep) over a shared graph via
 * {@link MockEscapeState}.
 */
class EscapeIntegrationTest {

  /**
   * The overriding requirement is that the explorer always finishes standing on
   * the exit. Given ample time on the linear graph, escape() must end on D.
   */
  @Test
  void escapeFinishesStandingOnTheExit() {
    Map<String, Node> g = GraphHelper.linearGraph();
    MockEscapeState state = new MockEscapeState(g.get("A"), g.get("D"), g.values(), 100);

    new Explorer().escape(state);

    assertEquals(g.get("D"), state.getCurrentNode(), "escape must return with the explorer on the exit");
  }

  /**
   * On the way out the explorer should bank the gold it steps onto. With ample
   * time on the linear graph both gold tiles B (100) and C (50) lie on the route
   * to the exit, so both must be collected (their remaining gold drops to 0).
   */
  @Test
  void escapeCollectsGoldAlongTheRoute() {
    Map<String, Node> g = GraphHelper.linearGraph();
    MockEscapeState state = new MockEscapeState(g.get("A"), g.get("D"), g.values(), 100);

    new Explorer().escape(state);

    assertEquals(0, g.get("B").getTile().getGold(), "gold on B should have been picked up");
    assertEquals(0, g.get("C").getTile().getGold(), "gold on C should have been picked up");
  }
}
