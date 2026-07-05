package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Explorer {

    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     * <p>
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     * <p>
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        Queue<Long> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        Map<Long, Long> parent = new HashMap<>();

        long start = state.getCurrentLocation();
        visited.add(start);
        queue.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            long target = queue.peek();
            moveTo(state, target, start, parent);

            if (state.getDistanceToTarget() == 0) {
                return;
            }

            queue.poll();
            long current = state.getCurrentLocation();

            for (NodeStatus neighbour : state.getNeighbours()) {
                long id = neighbour.nodeID();
                if (neighbour.distanceToTarget() == 0) {
                    state.moveTo(id);
                    return;
                }
                if (!visited.contains(id)) {
                    visited.add(id);
                    parent.put(id, current);
                    queue.add(id);
                }
            }
        }
    }

    private void moveTo(ExplorationState state, long target, long start, Map<Long, Long> parent) {
        long current = state.getCurrentLocation();
        if (current == target) {
            return;
        }

        List<Long> pathFromStart = new ArrayList<>();
        for (Long node = target; node != null; node = parent.get(node)) {
            pathFromStart.add(node);
        }
        Collections.reverse(pathFromStart);

        int currentIndex = pathFromStart.indexOf(current);
        if (currentIndex >= 0) {
            for (int i = currentIndex + 1; i < pathFromStart.size(); i++) {
                state.moveTo(pathFromStart.get(i));
            }
            return;
        }

        while (state.getCurrentLocation() != start) {
            state.moveTo(parent.get(state.getCurrentLocation()));
        }
        for (int i = 1; i < pathFromStart.size(); i++) {
            state.moveTo(pathFromStart.get(i));
        }
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        // TEMPORARY: shortest-path escape stub for end-to-end testing only.
        // Replace with the real gold-aware escape implementation later.
        List<Node> path = shortestPath(state.getCurrentNode(), state.getExit());
        for (int i = 1; i < path.size(); i++) {
            state.moveTo(path.get(i));
        }
    }

    private List<Node> shortestPath(Node start, Node goal) {
        Map<Long, Integer> dist = new HashMap<>();
        Map<Long, Node> parent = new HashMap<>();
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(
            node -> dist.getOrDefault(node.getId(), Integer.MAX_VALUE)
        ));

        dist.put(start.getId(), 0);
        parent.put(start.getId(), null);
        frontier.add(start);

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            int currentDist = dist.get(current.getId());
            if (current.equals(goal)) {
                break;
            }

            for (Node neighbour : current.getNeighbours()) {
                int nextDist = currentDist + current.getEdge(neighbour).length();
                if (nextDist < dist.getOrDefault(neighbour.getId(), Integer.MAX_VALUE)) {
                    dist.put(neighbour.getId(), nextDist);
                    parent.put(neighbour.getId(), current);
                    frontier.add(neighbour);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        for (Node node = goal; node != null; node = parent.get(node.getId())) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }
}
