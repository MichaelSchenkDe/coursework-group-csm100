package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import java.util.Map;

/**
 * The Explorer navigates the Temple of Gloom in two phases.
 * Exploration uses a biased depth-first search (ExploreSolver)
 * that prioritises neighbours closest to the Orb, preserving
 * a high bonus multiplier.
 * Escape uses a depth-limited branch evaluation strategy
 * (SelectNextTarget + TargetSearch) to maximise gold collection
 * while guaranteeing safe exit.
 */
public class Explorer {

    /**
     * Explore the cavern to find the Orb in as few steps as possible.
     * Delegates to ExploreSolver which implements a biased DFS,
     * sorting neighbours by distance to Orb at each step.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        ExploreSolver.solve(state);
    }

    /**
     * Escape from the cavern before the ceiling collapses,
     * collecting as much gold as possible along the way.
     * Pre-computes exit distances via Dijkstra, then at each step
     * evaluates all safe neighbour branches using depth-limited DFS
     * simulation to select the most gold-efficient move.
     * Falls back to shortest path to exit if no safe branch exists.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }

        Dijkstra dijkstra = new Dijkstra();
        DijkstraResult exitResult = dijkstra.computePath(state.getExit());
        Map<Node, Integer> exitDistanceMap = exitResult.getDistanceMap();

        SelectNextTarget targetSelector = new SelectNextTarget(exitDistanceMap);

        while (!state.getCurrentNode().equals(state.getExit())) {
            Node currNode = state.getCurrentNode();
            Node targetNode = targetSelector.selectBestTarget(
                currNode, state.getTimeRemaining()
            );

            if (targetNode == null) {
                // No safe gold branch — escape via shortest path
                while (!state.getCurrentNode().equals(state.getExit())) {
                    Node next = NextStep.nextStep(
                        state.getCurrentNode(), state.getExit(), exitResult
                    );
                    state.moveTo(next);
                    if (state.getCurrentNode().getTile().getGold() > 0) {
                        state.pickUpGold();
                    }
                }
                return;
            }

            state.moveTo(targetNode);
            if (state.getCurrentNode().getTile().getGold() > 0) {
                state.pickUpGold();
            }
        }
    }
}
