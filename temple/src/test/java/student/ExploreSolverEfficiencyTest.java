package student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checks that {@link ExploreSolver} does not waste moves on simple graphs
 * where a single path leads to the orb.
 */
class ExploreSolverEfficiencyTest {

    @Test
    void usesMinimumMovesOnLinearGraph() {
        MockExplorationState state = ExploreTestGraphs.linear();

        ExploreSolver.solve(state);

        assertEquals(3, state.moveHistory().size());
    }

    @Test
    void usesMinimumMovesOnZigZagGraph() {
        MockExplorationState state = ExploreTestGraphs.zigZag();

        ExploreSolver.solve(state);

        assertEquals(4, state.moveHistory().size());
    }

    @Test
    void usesNoMovesWhenOrbIsAtStart() {
        MockExplorationState state = ExploreTestGraphs.orbAtStart();

        ExploreSolver.solve(state);

        assertEquals(0, state.moveHistory().size());
    }
}
