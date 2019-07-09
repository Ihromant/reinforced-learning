package ua.ihromant.learning;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ua.ihromant.learning.Player.O;
import static ua.ihromant.learning.Player.X;

public class TicTacToeSizedTest {
    private static Player[] RAND = new Player[]{
            X,    X,    X,    O,    O,
            X,    X,    O,    O,    O,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
    };

    private static Player[] HOR_WIN = new Player[]{
            X,    X,    X,    X,    O,
            O,    X,    O,    O,    O,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
    };

    private static Player[] VER_WIN = new Player[]{
            X,    X,    X,    O,    O,
            X,    X,    O,    O,    O,
            null, X,    O,    null, null,
            null, X,    null, null, null,
            null, null, null, null, null,
    };

    private static Player[] NORD_WEST_WIN = new Player[]{
            X,    X,    X,    O,    O,
            X,    X,    O,    O,    O,
            null, O,    X,    null, null,
            null, null, null, X,    null,
            null, null, null, null, null,
    };

    private static Player[] NORD_EAST_WIN = new Player[]{
            X,    X,    X,    O,    O,
            X,    X,    O,    O,    O,
            X,    O,    X,    null, null,
            O,    null, null, null, null,
            null, null, null, null, null,
    };

    @Test
    public void testCurrent() {
        Assertions.assertEquals(X, TicTacToeStateSized.from(RAND).getCurrent());
        Assertions.assertEquals(X, TicTacToeStateSized.from(HOR_WIN).getCurrent());
        Assertions.assertEquals(O, TicTacToeStateSized.from(VER_WIN).getCurrent());
        Assertions.assertEquals(O, TicTacToeStateSized.from(NORD_WEST_WIN).getCurrent());
        Assertions.assertEquals(X, TicTacToeStateSized.from(NORD_EAST_WIN).getCurrent());
    }

    @Test
    public void testActions() {
        Assertions.assertEquals(15, TicTacToeStateSized.from(RAND).getActions().count());
        Assertions.assertEquals(0, TicTacToeStateSized.from(HOR_WIN).getActions().count());
        Assertions.assertEquals(0, TicTacToeStateSized.from(VER_WIN).getActions().count());
        Assertions.assertEquals(0, TicTacToeStateSized.from(NORD_WEST_WIN).getActions().count());
        Assertions.assertEquals(0, TicTacToeStateSized.from(NORD_EAST_WIN).getActions().count());
    }

    @Test
    public void testUtility() {
        Assertions.assertEquals(0.0, TicTacToeStateSized.from(RAND).getUtility(X));
        Assertions.assertEquals(1.0, TicTacToeStateSized.from(HOR_WIN).getUtility(X));
        Assertions.assertEquals(1.0, TicTacToeStateSized.from(VER_WIN).getUtility(X));
        Assertions.assertEquals(1.0, TicTacToeStateSized.from(NORD_WEST_WIN).getUtility(X));
        Assertions.assertEquals(-1.0, TicTacToeStateSized.from(NORD_EAST_WIN).getUtility(X));
        Assertions.assertEquals(0.0, TicTacToeStateSized.from(RAND).getUtility(O));
        Assertions.assertEquals(-1.0, TicTacToeStateSized.from(HOR_WIN).getUtility(O));
        Assertions.assertEquals(-1.0, TicTacToeStateSized.from(VER_WIN).getUtility(O));
        Assertions.assertEquals(-1.0, TicTacToeStateSized.from(NORD_WEST_WIN).getUtility(O));
        Assertions.assertEquals(1.0, TicTacToeStateSized.from(NORD_EAST_WIN).getUtility(O));
    }

    @Test
    public void testTerminal() {
        Assertions.assertFalse(TicTacToeStateSized.from(RAND).isTerminal());
        Assertions.assertTrue(TicTacToeStateSized.from(HOR_WIN).isTerminal());
        Assertions.assertTrue(TicTacToeStateSized.from(VER_WIN).isTerminal());
        Assertions.assertTrue(TicTacToeStateSized.from(NORD_WEST_WIN).isTerminal());
        Assertions.assertTrue(TicTacToeStateSized.from(NORD_EAST_WIN).isTerminal());
    }
}
