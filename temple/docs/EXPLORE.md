# Explore phase — Michael

Documentation for the **exploration** part of Temple of Gloom (finding the Orb).

---

## 1. What this code does

| Phase | Class | Algorithm |
|-------|-------|-----------|
| **Explore** | `ExploreSolver` | Guided DFS |
| **Wiring** | `Explorer.explore()` | Calls `ExploreSolver.solve(state)` |
| **Escape** | Not owned here | Handled by teammates |

**Goal:** Move through the maze, find the Orb, and return while standing on it.

**Score impact:** Fewer explore steps → higher **bonus multiplier** (up to 1.3).

---

## 2. Algorithm (guided DFS)

1. Mark the current tile as visited.
2. If `getDistanceToTarget() == 0`, you are on the Orb → stop.
3. Sort neighbours by Manhattan distance to the Orb (closest first).
4. For each unvisited neighbour:
   - move there
   - recurse
   - if Orb found, return
   - otherwise backtrack one step

This is depth-first search with a **heuristic**: always try the direction that looks closest to the Orb first.

---

## 3. Project layout (explore only)

```
temple/
├── src/main/java/student/
│   ├── ExploreSolver.java      ← explore algorithm
│   └── Explorer.java           ← explore() delegates here
├── src/test/java/student/explore/
│   ├── ExploreSolverCorrectnessTest.java
│   ├── ExploreSolverHeuristicTest.java
│   └── support/
│       ├── MockExplorationState.java
│       └── ExploreTestGraphs.java
└── docs/
    └── EXPLORE.md              ← this file
```

---

## 4. Commands

Run all commands from the **repo root** (`coursework-group-1`).

### Prerequisites

- Java 21 installed
- Use the included wrapper: `gradlew.bat` (Windows) or `./gradlew` (Mac/Linux)

### Compile

```powershell
.\gradlew.bat :temple:compileJava
```

### Run unit tests (explore)

```powershell
.\gradlew.bat :temple:test
```

Runs only the JUnit tests under `src/test/java/student/explore/`.

### Run the game — terminal (no UI)

Single run with a fixed seed:

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s 42"
```

100 runs on the same seed (team benchmark):

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-n 100 -s 42"
```

Random seed each run:

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain"
```

**Tip (PowerShell):** Always quote `-PchooseMain=...` and `--args=...` as shown. Otherwise PowerShell may split `main.TXTmain` incorrectly.

### Run the game — GUI (visual)

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.GUImain" "--args=-s 42"
```

Or with a random seed:

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.GUImain"
```

A Swing window opens showing the maze, explorer movement, bonus, time, and gold.

---

## 5. GUI setup (images)

The GUI loads PNG files from the classpath. They must live in:

```
temple/src/main/resources/
```

Required files (from `gui/Constants.java`):

| File | Purpose |
|------|---------|
| `orb.png` | Orb tile |
| `path.png` | Floor |
| `wall.png` | Wall |
| `entrance.png` | Entrance |
| `coins.png` | Gold |
| `notes.png` | Tasty tile |
| `info_texture.png` | UI background |
| `explorer_sprites.png` | Explorer sprite sheet |

If images are missing, the GUI may fail to start or show errors in the console.

---

## 6. Unit tests

### What we test

| Test class | What it checks |
|------------|----------------|
| `ExploreSolverCorrectnessTest` | Orb is always found (linear graph, branching graph, orb at start) |
| `ExploreSolverHeuristicTest` | Closer neighbours are tried first; misleading dead ends are explored and backtracked |

### How tests work

Tests use a **mock** `ExplorationState` (`MockExplorationState`) with small hand-made graphs (`ExploreTestGraphs`). No full game engine is needed.

### Example graphs

**Linear:** `1 — 2 — 3 — 4 (orb)`

**Branching:** `1 — 2 — 3 (orb)` and dead end at `4`

---

## 7. Benchmark seeds (team reference)

| Seed | Purpose | Typical avg score* |
|------|---------|-------------------|
| `42` | Team comparison seed | ~25 000 |
| `-4152836868077314850` | League-table high-score seed | ~63 000–69 000 |

\* Full score needs a real **escape** implementation (gold collection). Explore-only tests show **bonus multiplier** in the terminal output.

Check explore quality on seed 42:

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s 42"
```

Look for: `Bonus multiplier : 1.2` (or higher).

---

## 9. Quick reference

| Task | Command |
|------|---------|
| Compile | `.\gradlew.bat :temple:compileJava` |
| Test explore | `.\gradlew.bat :temple:test` |
| Play (terminal) | `.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s 42"` |
| Play (GUI) | `.\gradlew.bat ":temple:run" "-PchooseMain=main.GUImain" "--args=-s 42"` |
| Benchmark 100× | `.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-n 100 -s 42"` |
