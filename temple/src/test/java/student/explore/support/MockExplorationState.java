package student.explore.support;

import game.ExplorationState;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A small in-memory graph for testing {@link student.ExploreSolver}
 * without running the full game engine.
 */
public final class MockExplorationState implements ExplorationState {

    private long current;
    private final Map<Long, List<Long>> adjacency;
    private final Map<Long, Integer> distanceToOrb;
    private final List<Long> moveHistory = new ArrayList<>();

    public MockExplorationState(
        long start,
        Map<Long, List<Long>> adjacency,
        Map<Long, Integer> distanceToOrb
    ) {
        this.current = start;
        this.adjacency = adjacency;
        this.distanceToOrb = distanceToOrb;
    }

    @Override
    public long getCurrentLocation() {
        return current;
    }

    @Override
    public int getDistanceToTarget() {
        return distanceToOrb.getOrDefault(current, Integer.MAX_VALUE);
    }

    @Override
    public Collection<NodeStatus> getNeighbours() {
        List<NodeStatus> neighbours = new ArrayList<>();
        for (long id : adjacency.getOrDefault(current, Collections.emptyList())) {
            neighbours.add(new NodeStatus(id, distanceToOrb.getOrDefault(id, Integer.MAX_VALUE)));
        }
        return neighbours;
    }

    @Override
    public void moveTo(long id) {
        if (!adjacency.getOrDefault(current, Collections.emptyList()).contains(id)) {
            throw new IllegalArgumentException("Not a neighbour: " + id);
        }
        current = id;
        moveHistory.add(id);
    }

    public List<Long> moveHistory() {
        return List.copyOf(moveHistory);
    }

    public static Map<Long, List<Long>> adjacencyMap() {
        return new HashMap<>();
    }

    public static Map<Long, Integer> distanceMap() {
        return new HashMap<>();
    }

    public static void link(Map<Long, List<Long>> adjacency, long from, long to) {
        adjacency.computeIfAbsent(from, ignored -> new ArrayList<>()).add(to);
        adjacency.computeIfAbsent(to, ignored -> new ArrayList<>()).add(from);
    }
}
