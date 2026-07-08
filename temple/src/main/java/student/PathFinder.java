/**
 * NOTE: This class is superseded by Dijkstra.java and DijkstraResult.java
 * which provide the same shortest-path functionality with cleaner encapsulation
 * and immutable result objects.
 *
 * This file is retained because PathFinderTest depends on it for unit testing
 * Dijkstra's algorithm correctness on small known graphs.
 */


package student;

import game.Edge;
import game.Node;
import java.util.*;

public class PathFinder {
  public static class DijkstraResult {
    public final Map<Node, Integer> distance;
    public final Map<Node, Node> prev;

    DijkstraResult(Map<Node, Integer> distance, Map<Node, Node> prev) {
      this.distance = distance;
      this.prev = prev;
    }
  }

  private static class NodeDistance {
    final Node node;
    final int distance;

    NodeDistance(Node node, int distance) {
      this.node = node;
      this.distance = distance;
    }
  }

  public static DijkstraResult dijkstra(Node start) {
    Map<Node, Integer> distance = new HashMap<>();
    Map<Node, Node> prev = new HashMap<>();
    PriorityQueue<NodeDistance> pq = new PriorityQueue<>(
      Comparator.comparingInt(x -> x.distance)
    );

    distance.put(start, 0);
    pq.add(new NodeDistance(start, 0));

    while (!pq.isEmpty()) {
      NodeDistance curr = pq.poll();
      Node node = curr.node;

      if (curr.distance > distance.get(node)) {
        continue;
      }

      for (Node neighbour : node.getNeighbours()) {
        Edge edge = node.getEdge(neighbour);
        int newDist = distance.get(node) + edge.length();

        if (!distance.containsKey(neighbour) || newDist < distance.get(neighbour)) {
          distance.put(neighbour, newDist);
          prev.put(neighbour, node);
          pq.add(new NodeDistance(neighbour, newDist));
        }
      }
    }
    return new DijkstraResult(distance, prev);
  }

  public static List<Node> shortestPath(Node start, Node goal) {
    DijkstraResult result = dijkstra(start);
    return reconstructPath(start, goal, result.prev);
  }

  public static int shortestDistance(Node start, Node goal) {
    if (start.equals(goal)) {
      return 0;
    }
    DijkstraResult result = dijkstra(start);
    return result.distance.getOrDefault(goal, Integer.MAX_VALUE);
  }

  public static Node nextStep(Node start, Node target) {
    if (start.equals(target)) {
        return start;
    }

    DijkstraResult result = dijkstra(start);

    // Backtrack from target through prev map until we find
    // the node whose predecessor is start — that is the next step
    Node current = target;
    Node previous = result.prev.get(current);

    while (previous != null && !previous.equals(start)) {
        current = previous;
        previous = result.prev.get(current);
    }

    // If previous is null, target is unreachable — return start as fallback
    if (previous == null) {
        return start;
    }

    return current;
  } 

  public static int pathCost(List<Node> path) {
    int total = 0;
    for (int i=0; i < path.size() - 1; i++) {
      Edge edge = path.get(i).getEdge(path.get(i + 1));
      total += edge.length();
    }
    return total;
  }

  private static List<Node> reconstructPath(Node start, Node goal, Map<Node, Node> prev) {
    Deque<Node> path = new ArrayDeque<>();
    Node current = goal;

    if (!current.equals(start) && !prev.containsKey(current)) {
      return new ArrayList<>();
    }

    while (current != null) {
      path.addFirst(current);
      if (current.equals(start)) {
        break;
      }
      current = prev.get(current);
    }
    return new ArrayList<>(path);
  }


}


