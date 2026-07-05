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
 * its least-cost route. This class knows nothing about gold or time limits; it
 * only answers the question "what is the cheapest path from A to B?".
 */
public class PathFinder {

    /**
     * A tile paired with the best known cost to reach it. Ordered by that cost so
     * the priority queue always settles the cheapest candidate next.
     */
    private record Candidate(Node node, int cost) {
    }

    /**
     * Return the least-cost path from {@code source} to {@code target}, inclusive
     * of both endpoints and ordered from source to target.
     *
     * @param source the tile to start from
     * @param target the tile to reach
     * @return the ordered list of tiles forming the cheapest route
     */
    public List<Node> shortestPath(Node source, Node target) {
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
            if (current.node().equals(target)) {
                break;
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
        return reconstructPath(previous, source, target);
    }

    /**
     * Rebuild the path from source to target by walking the {@code previous}
     * links backwards from the target.
     *
     * @param previous map of each settled tile to the tile it was reached from
     * @param source   the start of the path
     * @param target   the end of the path
     * @return the ordered, immutable list of tiles from source to target
     */
    private List<Node> reconstructPath(Map<Node, Node> previous, Node source, Node target) {
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
