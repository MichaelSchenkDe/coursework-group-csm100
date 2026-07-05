package student;

import game.EscapeState;
import game.Node;

import java.util.List;

/**
 * Baseline escape strategy: walk the least-cost route straight to the exit,
 * collecting any gold that happens to lie on tiles along the way.
 * <p>
 * Correctness is the priority in the escape phase — returning anywhere other
 * than the exit, or running out of time, scores zero. The brief guarantees that
 * the shortest path out always fits within the time limit, so following the
 * least-cost route (computed by {@link PathFinder}) can never fail to escape.
 * Picking up gold on the current tile is free, so we grab it whenever it is
 * directly under us without ever stepping out of the way. This deliberately
 * leaves gold optimisation (safe detours) as a later improvement built on top
 * of this always-safe foundation.
 */
public class EscapeRouter {

    /** Computes the cheapest route out of the cavern. */
    private final PathFinder pathFinder = new PathFinder();

    /**
     * Escape the cavern, collecting gold that lies directly on the route.
     *
     * @param state the live escape state supplied by the game engine
     */
    public void escape(EscapeState state) {
        List<Node> route = pathFinder.shortestPath(state.getCurrentNode(), state.getExit());

        collectGold(state); // the starting tile
        for (Node next : route.subList(1, route.size())) {
            state.moveTo(next);
            collectGold(state); // every tile we step onto, including the exit
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
