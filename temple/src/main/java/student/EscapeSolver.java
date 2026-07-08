/**
 * NOTE: This class is superseded by the escape architecture introduced in
 * SelectNextTarget, TargetSearch, TargetSearchResult, Dijkstra, DijkstraResult,
 * NextStep and NodeDistance. Explorer.java now delegates escape logic directly
 * to SelectNextTarget rather than this class.
 *
 * This file is retained because EscapeSolverTest depends on it for unit testing
 * the gold ratio and safe detour logic in isolation.
 */


package student;

import game.EscapeState;
import game.Node;

import java.util.*;

/**
 * Solves the escape phase of the Temple of Gloom.
 * <p>
 * Strategy: pre-compute shortest distances from every node to the exit using
 * Dijkstra's algorithm. At each step, perform a limited-depth lookahead search
 * from the current position to find gold-bearing nodes within MAX_DEPTH steps.
 * Select the gold node with the best gold-per-total-cost ratio among those that
 * are safely reachable (cost to gold + cost from gold to exit <= time remaining).
 * If no safe gold node is found, follow the shortest path to the exit.
 * <p>
 * The lookahead depth of 12 was chosen empirically to balance gold discovery
 * against computation time, keeping execution well within the 10-second limit.
 */
public class EscapeSolver {

    /**
     * Maximum number of steps to look ahead when searching for gold nodes.
     * Higher values find more gold but increase computation time per step.
     * Empirically tuned to 12 for best score/time tradeoff.
     */
    private static final int MAX_DEPTH = 12;

    /**
     * Executes the escape strategy. Moves the explorer from the current
     * position to the exit, collecting gold along the way.
     * Guaranteed to reach the exit before time runs out.
     *
     * @param state the current escape state providing graph and movement API
     */
    public static void solve(EscapeState state) {
        Node exit = state.getExit();

        // Pre-compute shortest distances from every node to the exit once.
        // Since the graph is undirected, distance(node→exit) == distance(exit→node).
        PathFinder.DijkstraResult exitDistances = PathFinder.dijkstra(exit);

        while (!state.getCurrentNode().equals(exit)) {
            pickUpGoldIfPresent(state);

            Node target = selectBestTarget(state, exitDistances);
            Node next = PathFinder.nextStep(state.getCurrentNode(), target);
            state.moveTo(next);
        }

        pickUpGoldIfPresent(state);
    }

    /**
     * Selects the best node to head toward next.
     * <p>
     * Performs a limited-depth lookahead from the current position to discover
     * gold nodes within MAX_DEPTH steps. Among all safely reachable gold nodes,
     * selects the one with the highest gold-per-total-cost ratio where total cost
     * is distance(current→gold) + distance(gold→exit).
     * <p>
     * Falls back to the exit node if no safe gold detour exists.
     *
     * @param state         the current escape state
     * @param exitDistances pre-computed Dijkstra result rooted at the exit node
     * @return the best target node to head toward
     */
    static Node selectBestTarget(EscapeState state,
                                  PathFinder.DijkstraResult exitDistances) {
        Node current = state.getCurrentNode();
        Node exit = state.getExit();
        int timeLeft = state.getTimeRemaining();

        // Compute distances from current node to all reachable nodes
        PathFinder.DijkstraResult currDistances = PathFinder.dijkstra(current);

        // Collect gold nodes within MAX_DEPTH steps via limited DFS
        Set<Node> candidates = findGoldNodesWithinDepth(current, MAX_DEPTH);

        Node bestNode = exit;
        double bestRatio = 0.0;

        for (Node node : candidates) {
            int gold = node.getTile().getGold();
            if (gold == 0) {
                continue;
            }

            Integer toGold = currDistances.distance.get(node);
            Integer toExit = exitDistances.distance.get(node);

            // Skip if unreachable or if visiting would cause timeout
            if (toGold == null || toExit == null || (toGold + toExit) > timeLeft) {
                continue;
            }

            // Gold-per-total-cost ratio: high gold relative to full round-trip cost
            double ratio = (double) gold / (toGold + toExit);

            if (ratio > bestRatio) {
                bestRatio = ratio;
                bestNode = node;
            }
        }

        return bestNode;
    }

    /**
     * Performs a limited-depth BFS from the start node to find all gold-bearing
     * nodes reachable within the given depth limit.
     * <p>
     * Using BFS rather than DFS ensures we find nodes at minimum hop distance,
     * which combined with Dijkstra's weighted distances gives the best candidates.
     *
     * @param start    the node to search from
     * @param maxDepth maximum number of hops to explore
     * @return set of gold-bearing nodes within maxDepth hops of start
     */
    static Set<Node> findGoldNodesWithinDepth(Node start, int maxDepth) {
        Set<Node> goldNodes = new HashSet<>();
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Integer> depth = new HashMap<>();

        queue.add(start);
        visited.add(start);
        depth.put(start, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentDepth = depth.get(current);

            if (currentDepth >= maxDepth) {
                continue;
            }

            for (Node neighbour : current.getNeighbours()) {
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    depth.put(neighbour, currentDepth + 1);
                    queue.add(neighbour);

                    if (neighbour.getTile().getGold() > 0) {
                        goldNodes.add(neighbour);
                    }
                }
            }
        }

        // Also check start node itself
        if (start.getTile().getGold() > 0) {
            goldNodes.add(start);
        }

        return goldNodes;
    }

    /**
     * Picks up gold on the current tile if any is present.
     * Does nothing if the tile has no gold or gold has already been collected.
     *
     * @param state the current escape state
     */
    static void pickUpGoldIfPresent(EscapeState state) {
        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }
    }
}
