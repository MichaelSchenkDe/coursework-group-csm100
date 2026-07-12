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
}
