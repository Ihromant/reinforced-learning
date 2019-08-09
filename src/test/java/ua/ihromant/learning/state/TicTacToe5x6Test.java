package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ua.ihromant.learning.state.Player.O;
import static ua.ihromant.learning.state.Player.X;

public class TicTacToe5x6Test {
	private static Player[] RAND = new Player[]{
			X,    X,    X,    O,    O,    null,
			X,    X,    O,    O,    O,    null,
			null, null, null, null, null, null,
			null, null, null, null, null, null,
			null, null, null, null, null, null,
	};

	private static Player[] HOR_WIN = new Player[]{
			X,    X,    X,    X,    O,    null,
			O,    X,    O,    O,    O,    null,
			null, null, null, null, null, null,
			null, null, null, null, null, null,
			null, null, null, null, null, null,
	};

	private static Player[] VER_WIN = new Player[]{
			X,    X,    X,    O,    O,    null,
			X,    X,    O,    O,    O,    null,
			null, X,    O,    null, null, null,
			null, X,    null, null, null, null,
			null, null, null, null, null, null,
	};

	private static Player[] NORD_WEST_WIN = new Player[]{
			X,    X,    X,    O,    O,    null,
			X,    X,    O,    O,    O,    null,
			null, O,    X,    null, null, null,
			null, null, null, X,    null, null,
			null, null, null, null, null, null,
	};

	private static Player[] NORD_EAST_WIN = new Player[]{
			X,    X,    X,    O,    O,    null,
			X,    X,    O,    O,    O,    null,
			X,    O,    X,    null, null, null,
			O,    null, null, null, null, null,
			null, null, null, null, null, null,
	};

	@Test
	public void testCurrent() {
		Assertions.assertEquals(X, TicTacToeState5x6.from(RAND).getCurrent());
		Assertions.assertEquals(X, TicTacToeState5x6.from(HOR_WIN).getCurrent());
		Assertions.assertEquals(O, TicTacToeState5x6.from(VER_WIN).getCurrent());
		Assertions.assertEquals(O, TicTacToeState5x6.from(NORD_WEST_WIN).getCurrent());
		Assertions.assertEquals(X, TicTacToeState5x6.from(NORD_EAST_WIN).getCurrent());
	}

	@Test
	public void testActions() {
		Assertions.assertEquals(20, TicTacToeState5x6.from(RAND).getActs().count());
		Assertions.assertEquals(0, TicTacToeState5x6.from(HOR_WIN).getActs().count());
		Assertions.assertEquals(0, TicTacToeState5x6.from(VER_WIN).getActs().count());
		Assertions.assertEquals(0, TicTacToeState5x6.from(NORD_WEST_WIN).getActs().count());
		Assertions.assertEquals(0, TicTacToeState5x6.from(NORD_EAST_WIN).getActs().count());
	}

	@Test
	public void testUtility() {
		Assertions.assertEquals(0.5, TicTacToeState5x6.from(RAND).getUtility(X));
		Assertions.assertEquals(1.0, TicTacToeState5x6.from(HOR_WIN).getUtility(X));
		Assertions.assertEquals(1.0, TicTacToeState5x6.from(VER_WIN).getUtility(X));
		Assertions.assertEquals(1.0, TicTacToeState5x6.from(NORD_WEST_WIN).getUtility(X));
		Assertions.assertEquals(0.0, TicTacToeState5x6.from(NORD_EAST_WIN).getUtility(X));
		Assertions.assertEquals(0.5, TicTacToeState5x6.from(RAND).getUtility(O));
		Assertions.assertEquals(0.0, TicTacToeState5x6.from(HOR_WIN).getUtility(O));
		Assertions.assertEquals(0.0, TicTacToeState5x6.from(VER_WIN).getUtility(O));
		Assertions.assertEquals(0.0, TicTacToeState5x6.from(NORD_WEST_WIN).getUtility(O));
		Assertions.assertEquals(1.0, TicTacToeState5x6.from(NORD_EAST_WIN).getUtility(O));
	}

	@Test
	public void testTerminal() {
		Assertions.assertFalse(TicTacToeState5x6.from(RAND).isTerminal());
		Assertions.assertTrue(TicTacToeState5x6.from(HOR_WIN).isTerminal());
		Assertions.assertTrue(TicTacToeState5x6.from(VER_WIN).isTerminal());
		Assertions.assertTrue(TicTacToeState5x6.from(NORD_WEST_WIN).isTerminal());
		Assertions.assertTrue(TicTacToeState5x6.from(NORD_EAST_WIN).isTerminal());
	}
}