package student;

import game.GraphHelper;
import game.Node;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TargetSearch and SelectNextTarget using GraphHelper graphs.
 * Tests edge cases in Ajay's branch evaluation architecture.
 */
public class TargetSearchTest {

    /**
     * Builds exit distance map from a given exit node using Dijkstra.
     */
    private java.util.Map<Node, Integer> exitDistances(Node exit) {
        return new Dijkstra().computePath(exit).getDistanceMap();
    }

    @Test
    void selectBestTargetReturnsNullWhenNoSafeBranch() {
        Map<String, Node> g = GraphHelper.timeConstrainedGraph();
        var exitDists = exitDistances(g.get("Exit"));
        SelectNextTarget selector = new SelectNextTarget(exitDists);
        // Time=1: not enough to reach any neighbour safely
        // Exit costs 2, Gold costs 10, both exceed remaining time
        Node result = selector.selectBestTarget(g.get("Start"), 1);
        assertNull(result);
    }

    @Test
    void selectBestTargetFindsGoldWhenSafe() {
        Map<String, Node> g = GraphHelper.timeConstrainedGraph();
        // Time=25: enough to do Start->Gold->Exit (cost 20)
        var exitDists = exitDistances(g.get("Exit"));
        SelectNextTarget selector = new SelectNextTarget(exitDists);
        Node result = selector.selectBestTarget(g.get("Start"), 25);
        assertNotNull(result);
        assertEquals(g.get("Gold"), result);
    }

    @Test
    void selectBestTargetReturnsNullOnNoGoldGraph() {
        Map<String, Node> g = GraphHelper.noGoldGraph();
        var exitDists = exitDistances(g.get("C"));
        SelectNextTarget selector = new SelectNextTarget(exitDists);
        Node result = selector.selectBestTarget(g.get("A"), 0);
        assertNull(result);
    }

    @Test
    void selectBestTargetPrefersHigherGoldRatio() {
        Map<String, Node> g = GraphHelper.diamondGraph();
        // B: gold=200, cost to reach=1, exit cost=1, ratio=200/2=100
        // C: gold=10, cost to reach=3, exit cost=1, ratio=10/4=2.5
        var exitDists = exitDistances(g.get("D"));
        SelectNextTarget selector = new SelectNextTarget(exitDists);
        Node result = selector.selectBestTarget(g.get("A"), 100);
        assertEquals(g.get("B"), result);
    }

    @Test
    void targetSearchEvaluatesDeepGold() {
        Map<String, Node> g = GraphHelper.deepGoldGraph();
        var exitDists = exitDistances(g.get("Exit"));
        TargetSearch search = new TargetSearch(exitDists);
        // Evaluate from Mid1 with plenty of time
        Node mid1 = g.get("Mid1");
        int travelCost = 1;
        int remainingTime = 100;
        TargetSearchResult result = search.evaluateTarget(mid1, remainingTime, travelCost);
        // Should discover GoldDeep (500 gold) within depth 12
        assertTrue(result.getTotalGold() > 0);
    }

    @Test
    void targetSearchRespectsDepthLimit() {
        Map<String, Node> g = GraphHelper.deepGoldGraph();
        var exitDists = exitDistances(g.get("Exit"));
        TargetSearch search = new TargetSearch(exitDists);
        Node start = g.get("Start");
        TargetSearchResult result = search.evaluateTarget(start, 100, 1);
        // Search should complete without error regardless of depth
        assertNotNull(result);
    }

    @Test
    void equalRatioGraphPrefersLowerTravelCost() {
        Map<String, Node> g = GraphHelper.equalRatioGraph();
        // GoldA: gold=100, cost=1, ratio=100. GoldB: gold=200, cost=2, ratio=100.
        // Equal ratio — lower travel cost (GoldA) should win
        var exitDists = exitDistances(g.get("Exit"));
        SelectNextTarget selector = new SelectNextTarget(exitDists);
        Node result = selector.selectBestTarget(g.get("Start"), 100);
        assertEquals(g.get("GoldA"), result);
    }

    @Test
    void nextStepReturnsAdjacentNodeOnLinearGraph() {
        Map<String, Node> g = GraphHelper.linearGraph();
        DijkstraResult result = new Dijkstra().computePath(g.get("A"));
        Node next = NextStep.nextStep(g.get("A"), g.get("D"), result);
        assertEquals(g.get("B"), next);
        assertTrue(g.get("A").getNeighbours().contains(next));
    }

    @Test
    void nextStepReturnsCurrentWhenAlreadyAtTarget() {
        Map<String, Node> g = GraphHelper.linearGraph();
        DijkstraResult result = new Dijkstra().computePath(g.get("A"));
        Node next = NextStep.nextStep(g.get("A"), g.get("A"), result);
        assertEquals(g.get("A"), next);
    }

    @Test
    void dijkstraComputesCorrectDistances() {
        Map<String, Node> g = GraphHelper.linearGraph();
        DijkstraResult result = new Dijkstra().computePath(g.get("A"));
        assertEquals(0, result.getDistance(g.get("A")));
        assertEquals(1, result.getDistance(g.get("B")));
        assertEquals(2, result.getDistance(g.get("C")));
        assertEquals(3, result.getDistance(g.get("D")));
    }
}
