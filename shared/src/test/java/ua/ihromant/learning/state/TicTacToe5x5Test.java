package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ua.ihromant.learning.state.Player.O;
import static ua.ihromant.learning.state.Player.X;

public class TicTacToe5x5Test {
    private static final Player[] RAND = new Player[]{
            X,    X,    X,    O,    O,
            X,    X,    O,    O,    O,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
    };

    private static final Player[] DRAW = new Player[]{
            X,    X,    X,    O,    O,
            X,    X,    O,    O,    O,
            O,    O,    X,    X,    O,
            X,    O,    X,    O,    X,
            O,    X,    X,    X,    O,
    };

    private static final Player[] HOR_WIN = new Player[]{
            X,    X,    X,    X,    O,
            O,    X,    O,    O,    O,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
    };

    private static final Player[] VER_WIN = new Player[]{
            X,    X,    X,    O,    O,
            X,    X,    O,    O,    O,
            null, X,    O,    null, null,
            null, X,    null, null, null,
            null, null, null, null, null,
    };

    private static final Player[] NORD_WEST_WIN = new Player[]{
            X,    X,    X,    O,    O,
            X,    X,    O,    O,    O,
            null, O,    X,    null, null,
            null, null, null, X,    null,
            null, null, null, null, null,
    };

    private static final Player[] NORD_EAST_WIN = new Player[]{
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
        Assertions.assertEquals(15, TicTacToeState5x5.from(RAND).getActions().count());
    }

    @Test
    public void testUtility() {
        Assertions.assertNull(TicTacToeState5x5.from(RAND).getResult());
        Assertions.assertEquals(0.97, TicTacToeState5x5.from(HOR_WIN).getResult().getUtility(X));
        Assertions.assertEquals(0.94, TicTacToeState5x5.from(VER_WIN).getResult().getUtility(X));
        Assertions.assertEquals(0.94, TicTacToeState5x5.from(NORD_WEST_WIN).getResult().getUtility(X));
        Assertions.assertEquals(0.07, TicTacToeState5x5.from(NORD_EAST_WIN).getResult().getUtility(X));
        Assertions.assertEquals(0.03, TicTacToeState5x5.from(HOR_WIN).getResult().getUtility(O));
        Assertions.assertEquals(0.06, TicTacToeState5x5.from(VER_WIN).getResult().getUtility(O));
        Assertions.assertEquals(0.06, TicTacToeState5x5.from(NORD_WEST_WIN).getResult().getUtility(O));
        Assertions.assertEquals(0.93, TicTacToeState5x5.from(NORD_EAST_WIN).getResult().getUtility(O), 0.000001);
        Assertions.assertEquals(0.5, TicTacToeState5x5.from(DRAW).getResult().getUtility(X));
    }
}
