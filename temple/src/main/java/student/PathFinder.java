package student;

import game.Edge;
import game.Node;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Computes least-cost routes through the weighted cavern graph using Dijkstra's
 * algorithm.
 * <p>
 * During the escape phase every edge has a weight (the number of steps it costs
 * to traverse), so the shortest route out is not simply the one with the fewest
 * tiles. Dijkstra's algorithm expands tiles in order of the cheapest known cost
 * to reach them, which guarantees the first time a tile is settled we have found
 * its least-cost route.
 * <p>
 * A single run of Dijkstra from one source tile yields the cheapest cost to
 * <em>every</em> other tile at once. That whole result is captured in a
 * {@link Distances} object so a caller can both ask "how far is tile X?" and
 * "give me the actual path to tile X" without re-running the search.
 */
public class PathFinder {

    /**
     * A tile paired with the best known cost to reach it. Ordered by that cost so
     * the priority queue always settles the cheapest candidate next.
     */
    private record Candidate(Node node, int cost) {
    }

    /**
     * The result of running Dijkstra from a single source tile: the least cost to
     * reach every reachable tile, plus enough information to rebuild the path.
     */
    public static final class Distances {
        private final Node source;
        private final Map<Node, Integer> costToReach;
        private final Map<Node, Node> previous;

        private Distances(Node source, Map<Node, Integer> costToReach, Map<Node, Node> previous) {
            this.source = source;
            this.costToReach = costToReach;
            this.previous = previous;
        }

        /**
         * Return the least-cost number of steps from the source to {@code target},
         * or {@link Integer#MAX_VALUE} if the target is unreachable.
         *
         * @param target the tile to measure the distance to
         * @return the cheapest cost to reach {@code target}
         */
        public int costTo(Node target) {
            return costToReach.getOrDefault(target, Integer.MAX_VALUE);
        }

        /**
         * Return the least-cost path from the source to {@code target}, inclusive
         * of both endpoints and ordered from source to target.
         *
         * @param target the tile to build a path to
         * @return the ordered, immutable list of tiles from source to target
         */
        public List<Node> pathTo(Node target) {
            Deque<Node> path = new ArrayDeque<>();
            Node step = target;
            while (step != null && !step.equals(source)) {
                path.addFirst(step);
                step = previous.get(step);
            }
            path.addFirst(source);
            return List.copyOf(path);
        }
    }

    /**
     * Run Dijkstra's algorithm from {@code source} across the whole reachable
     * graph.
     *
     * @param source the tile to measure all distances from
     * @return the distances and paths from {@code source} to every reachable tile
     */
    public Distances from(Node source) {
        Map<Node, Integer> costToReach = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Candidate> frontier =
            new PriorityQueue<>(Comparator.comparingInt(Candidate::cost));

        costToReach.put(source, 0);
        frontier.add(new Candidate(source, 0));

        while (!frontier.isEmpty()) {
            Candidate current = frontier.poll();
            if (current.cost() > costToReach.getOrDefault(current.node(), Integer.MAX_VALUE)) {
                continue; // stale entry: a cheaper route to this tile was already found
            }
            for (Edge exit : current.node().getExits()) {
                Node neighbour = exit.getOther(current.node());
                int cost = current.cost() + exit.length();
                if (cost < costToReach.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    costToReach.put(neighbour, cost);
                    previous.put(neighbour, current.node());
                    frontier.add(new Candidate(neighbour, cost));
                }
            }
        }
        return new Distances(source, costToReach, previous);
    }

    /**
     * Convenience helper: return the least-cost path from {@code source} to
     * {@code target}, inclusive of both endpoints.
     *
     * @param source the tile to start from
     * @param target the tile to reach
     * @return the ordered list of tiles forming the cheapest route
     */
    public List<Node> shortestPath(Node source, Node target) {
        return from(source).pathTo(target);
    }
}
