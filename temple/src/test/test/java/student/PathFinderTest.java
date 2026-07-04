package student;

import game.GraphHelper;
import game.Node;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PathFinder.
 * Tests Dijkstra's algorithm for correctness on small known graphs.
 */
public class PathFinderTest {

    @Test
    void shortestPathLinearGraphFromAToD() {
        Map<String, Node> g = GraphHelper.linearGraph();
        List<Node> path = PathFinder.shortestPath(g.get("A"), g.get("D"));
        assertEquals(4, path.size());
        assertEquals(g.get("A"), path.get(0));
        assertEquals(g.get("D"), path.get(3));
    }

    @Test
    void shortestPathStartEqualsGoalReturnsSingleElement() {
        Map<String, Node> g = GraphHelper.linearGraph();
        List<Node> path = PathFinder.shortestPath(g.get("A"), g.get("A"));
        assertEquals(1, path.size());
        assertEquals(g.get("A"), path.get(0));
    }

    @Test
    void shortestDistanceLinearGraphIsCorrect() {
        Map<String, Node> g = GraphHelper.linearGraph();
        int dist = PathFinder.shortestDistance(g.get("A"), g.get("D"));
        assertEquals(3, dist);
    }

    @Test
    void shortestDistanceToSelfIsZero() {
        Map<String, Node> g = GraphHelper.linearGraph();
        assertEquals(0, PathFinder.shortestDistance(g.get("B"), g.get("B")));
    }

    @Test
    void shortestPathDiamondPrefersLowerWeightPath() {
        Map<String, Node> g = GraphHelper.diamondGraph();
        // A-B-D costs 2, A-C-D costs 4 — should choose A-B-D
        List<Node> path = PathFinder.shortestPath(g.get("A"), g.get("D"));
        assertTrue(path.contains(g.get("B")));
        assertFalse(path.contains(g.get("C")));
    }

    @Test
    void shortestDistanceDiamondIsTwo() {
        Map<String, Node> g = GraphHelper.diamondGraph();
        assertEquals(2, PathFinder.shortestDistance(g.get("A"), g.get("D")));
    }

    @Test
    void pathCostIsCorrectForLinearPath() {
        Map<String, Node> g = GraphHelper.linearGraph();
        List<Node> path = PathFinder.shortestPath(g.get("A"), g.get("D"));
        assertEquals(3, PathFinder.pathCost(path));
    }

    @Test
    void nextStepReturnsAdjacentNode() {
        Map<String, Node> g = GraphHelper.linearGraph();
        Node next = PathFinder.nextStep(g.get("A"), g.get("D"));
        // Next step from A toward D should be B (adjacent to A)
        assertEquals(g.get("B"), next);
        assertTrue(g.get("A").getNeighbours().contains(next));
    }

    @Test
    void nextStepWhenAlreadyAtTargetReturnsTarget() {
        Map<String, Node> g = GraphHelper.linearGraph();
        Node next = PathFinder.nextStep(g.get("A"), g.get("A"));
        assertEquals(g.get("A"), next);
    }

    @Test
    void dijkstraResultContainsAllNodes() {
        Map<String, Node> g = GraphHelper.linearGraph();
        PathFinder.DijkstraResult result = PathFinder.dijkstra(g.get("A"));
        assertTrue(result.distance.containsKey(g.get("A")));
        assertTrue(result.distance.containsKey(g.get("B")));
        assertTrue(result.distance.containsKey(g.get("C")));
        assertTrue(result.distance.containsKey(g.get("D")));
    }
}
