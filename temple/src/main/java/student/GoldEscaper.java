package student;

import game.EscapeState;
import game.Node;

import java.util.List;

/**
 * Escape strategy that collects as much gold as it safely can, then leaves.
 * <p>
 * It is built entirely on Dijkstra shortest paths ({@link PathFinder}) and keeps
 * one hard safety rule: <em>only detour to a gold tile if, after reaching it, the
 * shortest path from there to the exit still fits in the remaining time.</em>
 * Because moving to a tile along its shortest path costs exactly its Dijkstra
 * distance, this invariant guarantees the explorer can always still reach the
 * exit in time — so the escape can never fail.
 * <p>
 * Within that safety budget it is greedy: each round it heads for the gold tile
 * offering the best gold-per-step value. When no affordable gold tile remains,
 * it walks the shortest path to the exit. This typically collects far more gold
 * than the plain shortest-path escape while remaining just as safe.
 *
 * @see EscapeRouter for the simpler, always-safe shortest-path baseline
 */
public class GoldEscaper {

    /** Provides the Dijkstra searches this strategy is built on. */
    private final PathFinder pathFinder = new PathFinder();

    /**
     * Escape the cavern, taking safe detours to collect gold along the way.
     *
     * @param state the live escape state supplied by the game engine
     */
    public void escape(EscapeState state) {
        Node exit = state.getExit();
        // Edge weights never change during the escape, so the cost from any tile
        // to the exit is fixed and can be computed once.
        PathFinder.Distances toExit = pathFinder.from(exit);

        collectGold(state); // the starting tile
        while (true) {
            Node current = state.getCurrentNode();
            PathFinder.Distances fromCurrent = pathFinder.from(current);
            Node target = bestAffordableGold(state, fromCurrent, toExit);
            if (target == null) {
                break; // nothing worth (or safe) detouring for
            }
            follow(state, fromCurrent.pathTo(target));
        }
        follow(state, pathFinder.shortestPath(state.getCurrentNode(), exit));
    }

    /**
     * Choose the gold tile with the best gold-per-step value that we can still
     * afford to visit without jeopardising the escape.
     *
     * @param state       the live escape state (for time budget and gold amounts)
     * @param fromCurrent Dijkstra distances from the explorer's current tile
     * @param toExit      Dijkstra distances from the exit tile
     * @return the best gold tile to head for next, or {@code null} if none is
     *     both worthwhile and safe
     */
    private Node bestAffordableGold(EscapeState state,
                                    PathFinder.Distances fromCurrent,
                                    PathFinder.Distances toExit) {
        Node best = null;
        double bestValue = 0.0;
        for (Node node : state.getVertices()) {
            int gold = node.getTile().getGold();
            if (gold == 0) {
                continue;
            }
            int detour = fromCurrent.costTo(node);
            if (detour == 0 || detour == Integer.MAX_VALUE) {
                continue; // current tile (already collected) or unreachable
            }
            // Safety: after reaching this tile we must still be able to escape.
            if (detour + toExit.costTo(node) > state.getTimeRemaining()) {
                continue;
            }
            double value = (double) gold / detour;
            if (value > bestValue) {
                bestValue = value;
                best = node;
            }
        }
        return best;
    }

    /**
     * Walk along the given path, moving tile by tile and collecting any gold
     * found on each tile stepped onto.
     *
     * @param state the live escape state
     * @param path  the path to follow, starting at the current tile
     */
    private void follow(EscapeState state, List<Node> path) {
        for (Node next : path.subList(1, path.size())) {
            state.moveTo(next);
            collectGold(state);
        }
    }

    /**
     * Pick up the gold on the explorer's current tile, if any is present.
     *
     * @param state the live escape state
     */
    private void collectGold(EscapeState state) {
        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }
    }
}
