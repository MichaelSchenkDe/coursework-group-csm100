# Temple of Gloom - Group Coursework (CSM100)

Our solution to the Temple of Gloom coursework. Jeremy Hunt explores an unknown
cavern to reach the Orb, then escapes with as much gold as he can carry before
the ceiling collapses. The score is the gold collected multiplied by the
exploration bonus, so both phases matter.

## Team
- Marijke
- Ajay
- Michael
- Jordy

## Links
- Trello board: https://trello.com/invite/b/6a30f5910fdb37f934d44899/ATTIbf1db34d20b211867dcd49d584a0f7734262067E/sdp-2026
- SCRUM meeting recordings: https://drive.google.com/drive/folders/1iRBGD-tmfkCqoGuVN1YM2RyL9LH-Eq3c?usp=drive_link

## What we built

We only write two methods, both in `student/Explorer.java`: `explore()` and
`escape()`. Everything under `game`, `gui` and `main` is provided and we do not
touch it. To keep things readable we split the real work out of `Explorer` into
small classes in the `student` package, so each piece does one job and can be
tested on its own.

### Exploration

`explore()` has to reach the Orb in as few steps as possible for a good bonus.
We know the grid distance to the Orb from our current tile and each neighbour,
but not the walls, so it is a guided search rather than a blind one.

`ExploreSolver` runs a depth first search that always tries the neighbour with
the smallest distance to the Orb first. This heads roughly toward the target
while still backtracking out of dead ends, so the Orb is always found and the
step count stays low.

### Escape

After picking up the Orb the map is revealed, the walls shift, gold appears, and
every edge now has a weight (a step cost). `escape()` has to reach the exit
before time runs out. Reaching the exit is the priority, because failing scores
zero, and collecting gold is the bonus on top.

The escape is built from a few small classes:

- `Dijkstra`, `DijkstraResult` and `NodeDistance` compute the cheapest cost from
  one tile to every other tile, and the path to get there.
- `SelectNextTarget` and `TargetSearch` look a few steps ahead and choose the
  safe branch with the best gold per step.
- `NextStep` turns a chosen target into the single next move, since we can only
  step to an adjacent tile.

The rule that keeps it safe is simple. We only detour for gold if, after
reaching that gold, the shortest path from there to the exit still fits in the
time left. That way we can always still get out.

## Code structure (student package)

| Class | Job |
|---|---|
| `Explorer` | Entry point. Delegates `explore()` and `escape()`. |
| `ExploreSolver` | Guided depth first search for the exploration phase. |
| `Dijkstra`, `DijkstraResult`, `NodeDistance` | Shortest paths on the weighted escape graph. |
| `SelectNextTarget`, `TargetSearch`, `TargetSearchResult` | Pick the best safe gold branch to head for. |
| `NextStep` | The single adjacent move toward a chosen target. |

## Testing

The tests live under `temple/src/test/java` and run with JUnit 5. There are 19
in total, 5 for the exploration phase and 14 for the escape phase.

We build small graphs by hand and reuse them across tests instead of rebuilding
one in every test. The escape tests share a set of named maps in
`game/GraphHelper` (a straight line, a diamond, a time limited map, and so on),
and drive them through `MockEscapeState`, which fakes the game so we can set the
start tile, gold, exit and time and then check what the code does. The
exploration tests use `MockExplorationState` and the maps in `ExploreTestGraphs`
the same way.

What we cover:

- Exploration: the Orb is found on a straight path, on a branching map, and when
  we already start on it, and the guided search prefers closer tiles while still
  backing out of a misleading dead end.
- Escape: shortest paths pick the cheapest route by weight, unreachable tiles are
  handled, a safe gold branch is chosen, an unsafe detour is refused, gold is
  collected along the way including gold buried deep in the map, and the explorer
  always finishes on the exit.
- One test runs the real game over 30 seeded maps and checks the escape never
  fails, since that is the requirement we cannot get wrong.

Run the tests with:

```
gradle :temple:test
```

## Running the game

There are two entry points in the `main` package. `TXTmain` runs headless and
`GUImain` runs with the display.

```
gradle :temple:run -PchooseMain=main.TXTmain --args="-n 100"
gradle :temple:run -PchooseMain=main.GUImain --args="-s 42"
```

- `-n <count>` runs that many maps and prints an average (headless only).
- `-s <seed>` runs a specific map, which is handy for retrying a tricky one.

## Build

The project uses Gradle with a Java 21 toolchain. `gradle :temple:test` and
`gradle :temple:run` compile everything for you, so there is nothing else to set
up.
