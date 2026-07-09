package student.explore;

import org.junit.jupiter.api.Test;
import student.ExploreSolver;
import student.explore.support.ExploreTestGraphs;
import student.explore.support.MockExplorationState;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Correctness tests for {@link ExploreSolver}.
 * <p>
 * These tests only check that the orb is found and that the explorer
 * ends on the correct tile.
 */
class ExploreSolverCorrectnessTest {

    @Test
    void findsOrbOnLinearGraph() {
        MockExplorationState state = ExploreTestGraphs.linear();

        ExploreSolver.solve(state);

        assertEquals(0, state.getDistanceToTarget());
        assertEquals(4L, state.getCurrentLocation());
    }

    @Test
    void findsOrbOnBranchingGraph() {
        MockExplorationState state = ExploreTestGraphs.branchingWithDeadEnd();

        ExploreSolver.solve(state);

        assertEquals(0, state.getDistanceToTarget());
        assertEquals(3L, state.getCurrentLocation());
    }

    @Test
    void findsOrbWhenAlreadyAtStart() {
        MockExplorationState state = ExploreTestGraphs.orbAtStart();

        ExploreSolver.solve(state);

        assertEquals(0, state.getDistanceToTarget());
        assertEquals(1L, state.getCurrentLocation());
    }
}
