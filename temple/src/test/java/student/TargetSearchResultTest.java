package student;

import game.GraphHelper;
import game.Node;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TargetSearchResult comparison logic.
 */
public class TargetSearchResultTest {

    @Test
    void higherGoldRatioWins() {
        Map<String, Node> g = GraphHelper.linearGraph();
        TargetSearchResult high = new TargetSearchResult(100, 2, 1, g.get("A"), false);
        TargetSearchResult low  = new TargetSearchResult(10, 2, 1, g.get("B"), false);
        assertTrue(high.isBetterThan(low));
        assertFalse(low.isBetterThan(high));
    }

    @Test
    void lowerTravelCostWinsOnEqualRatio() {
        Map<String, Node> g = GraphHelper.linearGraph();
        TargetSearchResult cheap = new TargetSearchResult(10, 1, 1, g.get("A"), false);
        TargetSearchResult expensive = new TargetSearchResult(20, 2, 1, g.get("B"), false);
        assertTrue(cheap.isBetterThan(expensive));
    }

    @Test
    void higherTotalGoldWinsOnEqualRatioAndCost() {
    Map<String, Node> g = GraphHelper.linearGraph();
    // ratio = gold/cost: more = 200/2 = 100, less = 100/2 = 50
    // same cost, more gold, higher ratio — more wins
    TargetSearchResult more = new TargetSearchResult(200, 2, 1, g.get("A"), false);
    TargetSearchResult less = new TargetSearchResult(100, 2, 1, g.get("B"), false);
    assertTrue(more.isBetterThan(less));
    }

    @Test
    void continuableWinsOverNonContinuableOnTie() {
        Map<String, Node> g = GraphHelper.linearGraph();
        TargetSearchResult cont   = new TargetSearchResult(10, 1, 1, g.get("A"), true);
        TargetSearchResult noCont = new TargetSearchResult(10, 1, 1, g.get("B"), false);
        assertTrue(cont.isBetterThan(noCont));
    }

    @Test
    void goldRatioCalculatedCorrectly() {
        Map<String, Node> g = GraphHelper.linearGraph();
        TargetSearchResult r = new TargetSearchResult(100, 4, 1, g.get("A"), false);
        assertEquals(25.0, r.getGoldRatio(), 0.001);
    }

    @Test
    void goldRatioWithZeroCostReturnsGold() {
        Map<String, Node> g = GraphHelper.linearGraph();
        TargetSearchResult r = new TargetSearchResult(50, 0, 1, g.get("A"), false);
        assertEquals(50.0, r.getGoldRatio(), 0.001);
    }
}
