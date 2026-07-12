package student.explore;

import org.junit.jupiter.api.Test;
import student.ExploreSolver;
import student.explore.support.ExploreTestGraphs;
import student.explore.support.MockExplorationState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests on slightly larger hand-made graphs.
 */
class ExploreSolverGraphTest {

    @Test
    void findsOrbOnZigZagGraph() {
        MockExplorationState state = ExploreTestGraphs.zigZag();

        ExploreSolver.solve(state);

        assertEquals(0, state.getDistanceToTarget());
        assertEquals(5L, state.getCurrentLocation());
    }

    @Test
    void makesNoExtraMovesWhenOrbIsAtStart() {
        MockExplorationState state = ExploreTestGraphs.orbAtStart();

        ExploreSolver.solve(state);

        assertTrue(state.moveHistory().isEmpty());
    }
}
