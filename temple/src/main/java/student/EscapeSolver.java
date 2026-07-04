package student;

import game.EscapeState;
import game.Node;
import java.util.Collection;
import java.util.List;

/**
 * Solves the escape phase of the Temple of Gloom.
 * <p>
 * Stragegy: at each step, pick up any gold on the current tile, then look for
 * a gold detour among immediate neighbours. A detour is taken only if it is 
 * provably safe - the cost of reaching the gold tile plus the cost of reaching
 * the exit from thre must not exceed the remaining time budget. Among all safe
 * detours, the one with the highest gold-per-step ratio is chosen. If no safe
 * detour exists, the explorer follows the shortest path directlyto the exit.
 * <p>
 * This guarantees escape in all cases while maximising gold collection.
 */
public class EscapeSolver {
  /**
   * Executes the escape strategy. Moves the explorer from the current position
   * to the exit, collecting gold along the way.
   * @param state the current escape state, providing graph and movement api
   */
  public static void solve(EscapeState state) {
    Node exit = state.getExit();

    PathFinder.DijkstraResult exitDistances = PathFinder.dijkstra(exit);

    while (!state.getCurrentNode().equals(exit)) {
      pickUpGoldIfPresent(state);
      Node target = selectBestTarget(state, exitDistances);
      Node next = PathFinder.nextStep(state.getCurrentNode(), target);
      state.moveTo(next);
    }
    //Pick up gold on the exit tile itself if present
    pickUpGoldIfPresent(state);
  }

  /**
   * Chooses the next node to move to.
   * Prefers a safe gold detour among immediate neighbours if one exists;
   * otherwise follows the shortest path toward the exit.
   * 
   * @param state the current escape state 
   * @return the node to move to next 
   */
  static Node selectBestTarget(EscapeState state, PathFinder.DijkstraResult exitDistances) {
    Node current = state.getCurrentNode();
    Node exit = state.getExit();
    int timeLeft = state.getTimeRemaining();
    
    PathFinder.DijkstraResult currDistances = PathFinder.dijkstra(current);

    Node bestNode = exit;
    double bestRatio = 0.0;

    Collection<Node> allNodes = state.getVertices();

    for (Node node : allNodes) {
      int gold = node.getTile().getGold();

      if (gold == 0) {
        continue;
      }

      Integer toGold = currDistances.distance.get(node);
      Integer toExit = exitDistances.distance.get(node);

      if (toGold == null || toExit == null || (toGold + toExit) > timeLeft) {
        continue;
      }

      // Gold per total cost ratio: rewards high gold relative to full detour cost 
      double ratio = (double) gold / (toGold + toExit);
      if (ratio > bestRatio) {
        bestRatio = ratio;
        bestNode = node;
      }
    }
    return bestNode;
  }

  static void pickUpGoldIfPresent(EscapeState state){
    if (state.getCurrentNode().getTile().getGold() > 0) {
      state.pickUpGold();
    }
  }
}

