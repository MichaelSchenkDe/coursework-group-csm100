package student;

import static org.junit.jupiter.api.Assertions.assertFalse;

import game.GameState;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

/**
 * A property-style regression test over many real maps.
 * <p>
 * The brief makes escaping the single non-negotiable requirement: returning
 * anywhere but the exit, or running out of time, scores zero. This test runs the
 * actual game engine ({@link GameState#runNewGame}) headless across a spread of
 * seeds and asserts the escape never fails on any of them.
 * <p>
 * Failure cannot be detected from the returned score alone — gold banked before
 * running out of time still yields a positive score — so instead the engine's
 * error stream is captured and checked for the messages it prints when an escape
 * fails to end at the exit, runs out of steps, or throws.
 */
class AlwaysEscapesTest {

  /** Number of distinct seeded maps to run. */
  private static final int MAP_COUNT = 30;

  /** Substrings the engine prints to System.err only when an escape fails. */
  private static final String[] ESCAPE_FAILURE_MARKERS = {
    "escape ran out of steps",
    "escape failed to end at the stairs",
    "error during the escape phase",
  };

  /**
   * Run {@value #MAP_COUNT} seeded maps and require every escape to succeed.
   * Seeds 1..N give reproducible maps; the engine's noisy stdout is swallowed
   * and its stderr captured so we can inspect it for escape-failure markers.
   */
  @Test
  void escapesEverySeededMap() {
    PrintStream realOut = System.out;
    PrintStream realErr = System.err;
    ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
    PrintStream silentOut = new PrintStream(new ByteArrayOutputStream());

    try {
      System.setOut(silentOut);
      System.setErr(new PrintStream(capturedErr));
      for (long seed = 1; seed <= MAP_COUNT; seed++) {
        GameState.runNewGame(seed, false);
      }
    } finally {
      System.setOut(realOut);
      System.setErr(realErr);
    }

    String errors = capturedErr.toString();
    for (String marker : ESCAPE_FAILURE_MARKERS) {
      assertFalse(errors.contains(marker),
          "an escape failed across " + MAP_COUNT + " maps: found \"" + marker + "\"");
    }
  }
}
