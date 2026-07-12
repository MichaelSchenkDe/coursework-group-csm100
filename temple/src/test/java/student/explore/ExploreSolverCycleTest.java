package student.explore;

import org.junit.jupiter.api.Test;
import student.ExploreSolver;
import student.explore.support.ExploreTestGraphs;
import student.explore.support.MockExplorationState;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ensures guided DFS terminates on graphs with cycles (visited-set correctness).
 */
class ExploreSolverCycleTest {

    @Test
    void findsOrbOnGraphWithCycle() {
        MockExplorationState state = ExploreTestGraphs.cycleWithOrb();

        ExploreSolver.solve(state);

        assertEquals(0, state.getDistanceToTarget());
        assertEquals(3L, state.getCurrentLocation());
    }
}
