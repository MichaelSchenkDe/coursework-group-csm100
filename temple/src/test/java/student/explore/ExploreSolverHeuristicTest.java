package student.explore;

import org.junit.jupiter.api.Test;
import student.ExploreSolver;
import student.explore.support.ExploreTestGraphs;
import student.explore.support.MockExplorationState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Behaviour tests for the guided DFS heuristic in {@link ExploreSolver}.
 */
class ExploreSolverHeuristicTest {

    @Test
    void prefersCloserNeighbourFirst() {
        MockExplorationState state = ExploreTestGraphs.branchingWithDeadEnd();

        ExploreSolver.solve(state);

        int indexOfCloserNeighbour = state.moveHistory().indexOf(2L);
        int indexOfDeadEnd = state.moveHistory().indexOf(4L);

        assertTrue(indexOfCloserNeighbour >= 0);
        if (indexOfDeadEnd >= 0) {
            assertTrue(indexOfCloserNeighbour < indexOfDeadEnd);
        }
    }

    @Test
    void exploresMisleadingDeadEndBeforeFindingOrb() {
        MockExplorationState state = ExploreTestGraphs.misleadingDeadEnd();

        ExploreSolver.solve(state);

        assertTrue(state.moveHistory().contains(4L));
        assertEquals(0, state.getDistanceToTarget());
        assertEquals(3L, state.getCurrentLocation());
    }
}
