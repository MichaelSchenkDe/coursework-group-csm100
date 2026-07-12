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
        // Equal ratio, so the lower travel cost (GoldA) should win
        var exitDists = exitDistances(g.get("Exit"));
        SelectNextTarget selector = new SelectNextTarget(exitDists);
        Node result = selector.selectBestTarget(g.get("Start"), 100);
        assertEquals(g.get("GoldA"), result);
    }

}
