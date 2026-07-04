package student;

import game.GraphHelper;
import game.Node;
import game.EscapeState;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EscapeSolver.
 * Tests gold collection, detour selection, and safe escape logic
 * using small fixed graphs from GraphHelper.
 */
public class EscapeSolverTest {

    /**
     * Minimal mock EscapeState backed by a fixed graph.
     * Tracks current node and time remaining to simulate movement.
     */
    private static class MockEscapeState implements EscapeState {
        private Node current;
        private final Node exit;
        private final Collection<Node> vertices;
        private int timeRemaining;

        MockEscapeState(Node current, Node exit,
                        Collection<Node> vertices, int timeRemaining) {
            this.current = current;
            this.exit = exit;
            this.vertices = vertices;
            this.timeRemaining = timeRemaining;
        }

        @Override public Node getCurrentNode() { return current; }
        @Override public Node getExit() { return exit; }
        @Override public Collection<Node> getVertices() { return vertices; }
        @Override public int getTimeRemaining() { return timeRemaining; }

        @Override
        public void moveTo(Node n) {
            if (!current.getNeighbours().contains(n)) {
                throw new IllegalArgumentException("Not a neighbour");
            }
            timeRemaining -= current.getEdge(n).length();
            current = n;
        }

        @Override
        public void pickUpGold() {
            if (current.getTile().getGold() <= 0) {
                throw new IllegalStateException("No gold here");
            }
            current.getTile().takeGold();
        }
    }

    @Test
    void selectBestTargetChoosesGoldNodeWhenSafe() {
        Map<String, Node> g = GraphHelper.linearGraph();
        // A is start, D is exit, B has gold=100, C has gold=50
        // With plenty of time, should prefer gold over going straight to exit
        MockEscapeState state = new MockEscapeState(
            g.get("A"), g.get("D"), g.values(), 100
        );
        PathFinder.DijkstraResult exitDistances = PathFinder.dijkstra(g.get("D"));
        Node target = EscapeSolver.selectBestTarget(state, exitDistances);
        // Should not just go straight to exit when gold is safely reachable
        assertNotEquals(g.get("A"), target);
    }

    @Test
    void selectBestTargetReturnsExitWhenNoGoldPresent() {
        Map<String, Node> g = GraphHelper.linearGraph();
        // Pick up all gold first
        g.get("B").getTile().takeGold();
        g.get("C").getTile().takeGold();
        MockEscapeState state = new MockEscapeState(
            g.get("A"), g.get("D"), g.values(), 100
        );
        PathFinder.DijkstraResult exitDistances = PathFinder.dijkstra(g.get("D"));
        Node target = EscapeSolver.selectBestTarget(state, exitDistances);
        assertEquals(g.get("D"), target);
    }

    @Test
    void selectBestTargetReturnsExitWhenGoldDetourUnsafe() {
        Map<String, Node> g = GraphHelper.timeConstrainedGraph();
        // Time=3: only enough to go Start->Exit (cost 2). Gold detour costs 20.
        MockEscapeState state = new MockEscapeState(
            g.get("Start"), g.get("Exit"), g.values(), 3
        );
        PathFinder.DijkstraResult exitDistances = PathFinder.dijkstra(g.get("Exit"));
        Node target = EscapeSolver.selectBestTarget(state, exitDistances);
        assertEquals(g.get("Exit"), target);
    }

    @Test
    void pickUpGoldIfPresentCollectsGold() {
        Map<String, Node> g = GraphHelper.linearGraph();
        MockEscapeState state = new MockEscapeState(
            g.get("B"), g.get("D"), g.values(), 100
        );
        // B has gold=100
        assertEquals(100, state.getCurrentNode().getTile().getGold());
        EscapeSolver.pickUpGoldIfPresent(state);
        assertEquals(0, state.getCurrentNode().getTile().getGold());
    }

    @Test
    void pickUpGoldIfPresentDoesNothingWhenNoGold() {
        Map<String, Node> g = GraphHelper.linearGraph();
        MockEscapeState state = new MockEscapeState(
            g.get("A"), g.get("D"), g.values(), 100
        );
        // A has no gold — should not throw
        assertDoesNotThrow(() -> EscapeSolver.pickUpGoldIfPresent(state));
    }

    @Test
    void solveReachesExitWithoutError() {
        Map<String, Node> g = GraphHelper.linearGraph();
        MockEscapeState state = new MockEscapeState(
            g.get("A"), g.get("D"), g.values(), 100
        );
        EscapeSolver.solve(state);
        assertEquals(g.get("D"), state.getCurrentNode());
    }

    @Test
    void solveCollectsGoldAlongPath() {
        Map<String, Node> g = GraphHelper.linearGraph();
        MockEscapeState state = new MockEscapeState(
            g.get("A"), g.get("D"), g.values(), 100
        );
        EscapeSolver.solve(state);
        // After solving, gold tiles should be collected
        assertEquals(0, g.get("B").getTile().getGold());
    }

    @Test
    void selectBestTargetPrefersBetterGoldRatio() {
        Map<String, Node> g = GraphHelper.diamondGraph();
        // B has gold=200 at cost 1, C has gold=10 at cost 3
        // B ratio = 200/(1+1)=100, C ratio = 10/(3+1)=2.5 — should pick B
        MockEscapeState state = new MockEscapeState(
            g.get("A"), g.get("D"), g.values(), 100
        );
        PathFinder.DijkstraResult exitDistances = PathFinder.dijkstra(g.get("D"));
        Node target = EscapeSolver.selectBestTarget(state, exitDistances);
        assertEquals(g.get("B"), target);
    }
}
