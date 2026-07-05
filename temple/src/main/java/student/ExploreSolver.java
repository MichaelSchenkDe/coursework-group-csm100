package student;

import game.ExplorationState;
import game.NodeStatus;

import java.util.*;

/**
 * Solves the exploration phase of the Temple of Gloom.
 * <p>
 * Strategy: depth-first search biased toward neighbours with the smallest
 * distance to the Orb. At each step, unvisited neighbours are sorted by
 * their distance to the Orb ascending, so the explorer moves in roughly
 * the right direction while still being able to backtrack and explore
 * dead ends when necessary.
 * <p>
 * This guarantees the Orb is always found while keeping the number of
 * steps reasonably low, preserving a good bonus multiplier.
 */
public class ExploreSolver {

    /**
     * Executes the exploration strategy. Moves the explorer through the
     * cavern using a biased depth-first search until the Orb is found.
     * Returns when standing on the Orb tile (distanceToTarget == 0).
     *
     * @param state the current exploration state, providing movement and sensing API
     */
    public static void solve(ExplorationState state) {
        Set<Long> visited = new HashSet<>();
        dfs(state, visited);
    }

    /**
     * Recursive depth-first search that moves toward the Orb.
     * Neighbours are visited in order of increasing distance to the Orb,
     * so the explorer is biased toward the most promising direction.
     * Backtracking is handled naturally by the recursion stack.
     * NodeStatus implements Comparable by distanceToTarget so Collections.sort
     * orders them correctly without a custom comparator.
     *
     * @param state   the current exploration state
     * @param visited set of node IDs already visited, to avoid revisiting
     */
    private static void dfs(ExplorationState state, Set<Long> visited) {
        long currentId = state.getCurrentLocation();
        visited.add(currentId);

        // Base case: standing on the Orb
        if (state.getDistanceToTarget() == 0) {
            return;
        }

        // Sort neighbours by distanceToTarget ascending — greedy heuristic
        // NodeStatus.compareTo orders by distanceToTarget so Collections.sort works directly
        List<NodeStatus> neighbours = new ArrayList<>(state.getNeighbours());
        Collections.sort(neighbours);

        for (NodeStatus neighbour : neighbours) {
            if (visited.contains(neighbour.nodeID())) {
                continue;
            }

            // Move to this neighbour and explore from there
            state.moveTo(neighbour.nodeID());
            dfs(state, visited);

            // If Orb found during recursion, return immediately up the stack
            if (state.getDistanceToTarget() == 0) {
                return;
            }

            // Orb not found in that direction — backtrack to current node
            state.moveTo(currentId);
        }
    }
}
