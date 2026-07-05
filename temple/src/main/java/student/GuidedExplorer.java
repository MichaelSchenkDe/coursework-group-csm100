package student;

import game.ExplorationState;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exploration strategy that finds the Orb using a <em>heuristic-guided
 * depth-first search</em> with backtracking.
 * <p>
 * A plain depth-first search (the baseline suggested in the brief) always finds
 * the Orb but wanders, because it visits neighbours in an arbitrary order. We
 * keep the guarantee of depth-first search — every reachable tile is eventually
 * visited and dead ends are unwound by walking back the way we came — but at
 * each tile we try the neighbour that is <em>closest to the Orb first</em>,
 * using the grid distance hint from {@link ExplorationState#getDistanceToTarget()}.
 * This steers the search towards the Orb and typically reaches it in far fewer
 * steps, which raises the exploration bonus multiplier.
 * <p>
 * The instance is single-use: it keeps the set of visited tiles as state while a
 * single {@link #explore(ExplorationState)} call runs.
 */
public class GuidedExplorer {

    /** Identifiers of every tile we have already stood on, so we never revisit. */
    private final Set<Long> visited = new HashSet<>();

    /**
     * Walk the cavern until the explorer is standing on the Orb, at which point
     * the method returns so the game engine can pick the Orb up.
     *
     * @param state the live exploration state supplied by the game engine
     */
    public void explore(ExplorationState state) {
        search(state);
    }

    /**
     * Recursively search from the explorer's current tile.
     *
     * @param state the live exploration state
     * @return {@code true} once the explorer is standing on the Orb; {@code false}
     *     if this branch is a dead end and the caller should backtrack
     */
    private boolean search(ExplorationState state) {
        visited.add(state.getCurrentLocation());
        if (state.getDistanceToTarget() == 0) {
            return true; // standing on the Orb
        }

        for (NodeStatus neighbour : neighboursClosestFirst(state)) {
            if (visited.contains(neighbour.nodeID())) {
                continue;
            }
            long origin = state.getCurrentLocation();
            state.moveTo(neighbour.nodeID());
            if (search(state)) {
                return true;
            }
            state.moveTo(origin); // dead end: step back and try the next branch
        }
        return false;
    }

    /**
     * Return the current tile's neighbours ordered by increasing grid distance to
     * the Orb, so the most promising direction is explored first.
     *
     * @param state the live exploration state
     * @return a new list of neighbours, closest to the Orb first
     */
    private List<NodeStatus> neighboursClosestFirst(ExplorationState state) {
        List<NodeStatus> neighbours = new ArrayList<>(state.getNeighbours());
        neighbours.sort(Comparator.comparingInt(NodeStatus::distanceToTarget));
        return neighbours;
    }
}
