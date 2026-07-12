package student;

import game.EscapeState;
import game.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A hand-built {@link EscapeState} backed by a fixed graph, shared across the
 * escape-phase tests.
 * <p>
 * It mirrors the real engine's rules so the strategy under test behaves exactly
 * as it would in a game: {@link #moveTo(Node)} only accepts a neighbour of the
 * current node and decrements the remaining time by that edge's weight, and
 * {@link #pickUpGold()} throws if the current tile has no gold. Every visited
 * node is recorded so a test can assert the exact route taken.
 * <p>
 * Adapted from the mock embedded in the {@code marijke_escape} tests, extracted
 * so all escape tests reuse a single copy.
 */
final class MockEscapeState implements EscapeState {

  private Node current;
  private final Node exit;
  private final Collection<Node> vertices;
  private int timeRemaining;
  private final List<Node> visited = new ArrayList<>();

  MockEscapeState(Node start, Node exit, Collection<Node> vertices, int timeRemaining) {
    this.current = start;
    this.exit = exit;
    this.vertices = vertices;
    this.timeRemaining = timeRemaining;
    this.visited.add(start);
  }

  /** @return the ordered list of nodes stood on, starting with the start node. */
  List<Node> visited() {
    return visited;
  }

  @Override
  public Node getCurrentNode() {
    return current;
  }

  @Override
  public Node getExit() {
    return exit;
  }

  @Override
  public Collection<Node> getVertices() {
    return vertices;
  }

  @Override
  public int getTimeRemaining() {
    return timeRemaining;
  }

  @Override
  public void moveTo(Node n) {
    if (!current.getNeighbours().contains(n)) {
      throw new IllegalArgumentException("moveTo target is not a neighbour of the current node");
    }
    timeRemaining -= current.getEdge(n).length();
    current = n;
    visited.add(n);
  }

  @Override
  public void pickUpGold() {
    if (current.getTile().getGold() <= 0) {
      throw new IllegalStateException("no gold on the current tile");
    }
    current.getTile().takeGold();
  }
}
