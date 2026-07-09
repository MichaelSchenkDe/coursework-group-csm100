package student.explore.support;

import java.util.List;
import java.util.Map;

/**
 * Hand-crafted exploration graphs used across explore tests.
 * <p>
 * Node ids are simple numbers. Distance values mimic the Manhattan hint
 * returned by {@link game.ExplorationState#getNeighbours()}.
 */
public final class ExploreTestGraphs {

    private ExploreTestGraphs() {
    }

    /**
     * Linear path: 1 - 2 - 3 - 4(orb)
     */
    public static MockExplorationState linear() {
        Map<Long, List<Long>> adjacency = MockExplorationState.adjacencyMap();
        MockExplorationState.link(adjacency, 1L, 2L);
        MockExplorationState.link(adjacency, 2L, 3L);
        MockExplorationState.link(adjacency, 3L, 4L);

        Map<Long, Integer> distances = MockExplorationState.distanceMap();
        distances.put(1L, 3);
        distances.put(2L, 2);
        distances.put(3L, 1);
        distances.put(4L, 0);

        return new MockExplorationState(1L, adjacency, distances);
    }

    /**
     * Branch with a dead end:
     * <pre>
     *   1 - 2 - 3(orb)
     *    \
     *     4 (dead end, far from orb)
     * </pre>
     */
    public static MockExplorationState branchingWithDeadEnd() {
        Map<Long, List<Long>> adjacency = MockExplorationState.adjacencyMap();
        MockExplorationState.link(adjacency, 1L, 2L);
        MockExplorationState.link(adjacency, 1L, 4L);
        MockExplorationState.link(adjacency, 2L, 3L);

        Map<Long, Integer> distances = MockExplorationState.distanceMap();
        distances.put(1L, 2);
        distances.put(2L, 1);
        distances.put(3L, 0);
        distances.put(4L, 5);

        return new MockExplorationState(1L, adjacency, distances);
    }

    /**
     * Orb is already on the starting tile.
     */
    public static MockExplorationState orbAtStart() {
        Map<Long, List<Long>> adjacency = MockExplorationState.adjacencyMap();
        MockExplorationState.link(adjacency, 1L, 2L);

        Map<Long, Integer> distances = MockExplorationState.distanceMap();
        distances.put(1L, 0);
        distances.put(2L, 1);

        return new MockExplorationState(1L, adjacency, distances);
    }

    /**
     * Misleading dead end that looks closer to the orb than the real path:
     * <pre>
     *   1 - 4 (dead end, Manhattan hint = 1)
     *    \
     *     2 - 3(orb, hint from 1 = 2)
     * </pre>
     */
    public static MockExplorationState misleadingDeadEnd() {
        Map<Long, List<Long>> adjacency = MockExplorationState.adjacencyMap();
        MockExplorationState.link(adjacency, 1L, 2L);
        MockExplorationState.link(adjacency, 1L, 4L);
        MockExplorationState.link(adjacency, 2L, 3L);

        Map<Long, Integer> distances = MockExplorationState.distanceMap();
        distances.put(1L, 3);
        distances.put(2L, 2);
        distances.put(3L, 0);
        distances.put(4L, 1); // looks best from node 1, but 4 is a dead end

        return new MockExplorationState(1L, adjacency, distances);
    }
}
