package student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checks that {@link Explorer} correctly delegates to {@link student.ExploreSolver}.
 */
class ExplorerWiringTest {

    @Test
    void exploreDelegatesToExploreSolver() {
        Explorer explorer = new Explorer();
        MockExplorationState state = ExploreTestGraphs.linear();

        explorer.explore(state);

        assertEquals(0, state.getDistanceToTarget());
        assertEquals(4L, state.getCurrentLocation());
    }
}
