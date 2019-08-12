package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ua.ihromant.learning.state.Player.O;
import static ua.ihromant.learning.state.Player.X;

public class TicTacToe5x5Test {
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
        Assertions.assertEquals(X, TicTacToeState5x5.from(RAND).getCurrent());
        Assertions.assertEquals(X, TicTacToeState5x5.from(HOR_WIN).getCurrent());
        Assertions.assertEquals(O, TicTacToeState5x5.from(VER_WIN).getCurrent());
        Assertions.assertEquals(O, TicTacToeState5x5.from(NORD_WEST_WIN).getCurrent());
        Assertions.assertEquals(X, TicTacToeState5x5.from(NORD_EAST_WIN).getCurrent());
    }

    @Test
    public void testActions() {
        Assertions.assertEquals(15, TicTacToeState5x5.from(RAND).getActs().count());
        Assertions.assertEquals(0, TicTacToeState5x5.from(HOR_WIN).getActs().count());
        Assertions.assertEquals(0, TicTacToeState5x5.from(VER_WIN).getActs().count());
        Assertions.assertEquals(0, TicTacToeState5x5.from(NORD_WEST_WIN).getActs().count());
        Assertions.assertEquals(0, TicTacToeState5x5.from(NORD_EAST_WIN).getActs().count());
    }

    @Test
    public void testUtility() {
        Assertions.assertEquals(GameResult.DRAW, TicTacToeState5x5.from(RAND).getUtility(X));
        Assertions.assertEquals(GameResult.WIN, TicTacToeState5x5.from(HOR_WIN).getUtility(X));
        Assertions.assertEquals(GameResult.WIN, TicTacToeState5x5.from(VER_WIN).getUtility(X));
        Assertions.assertEquals(GameResult.WIN, TicTacToeState5x5.from(NORD_WEST_WIN).getUtility(X));
        Assertions.assertEquals(GameResult.LOSE, TicTacToeState5x5.from(NORD_EAST_WIN).getUtility(X));
        Assertions.assertEquals(GameResult.DRAW, TicTacToeState5x5.from(RAND).getUtility(O));
        Assertions.assertEquals(GameResult.LOSE, TicTacToeState5x5.from(HOR_WIN).getUtility(O));
        Assertions.assertEquals(GameResult.LOSE, TicTacToeState5x5.from(VER_WIN).getUtility(O));
        Assertions.assertEquals(GameResult.LOSE, TicTacToeState5x5.from(NORD_WEST_WIN).getUtility(O));
        Assertions.assertEquals(GameResult.WIN, TicTacToeState5x5.from(NORD_EAST_WIN).getUtility(O));
    }

    @Test
    public void testTerminal() {
        Assertions.assertFalse(TicTacToeState5x5.from(RAND).isTerminal());
        Assertions.assertTrue(TicTacToeState5x5.from(HOR_WIN).isTerminal());
        Assertions.assertTrue(TicTacToeState5x5.from(VER_WIN).isTerminal());
        Assertions.assertTrue(TicTacToeState5x5.from(NORD_WEST_WIN).isTerminal());
        Assertions.assertTrue(TicTacToeState5x5.from(NORD_EAST_WIN).isTerminal());
    }
}
