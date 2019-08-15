package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NimLineStateTest {
	private static final double[] standartModel =
				   {0, 0, 1,
					0, 1, 1,
					1, 0, 1,
					1, 1, 1,
					1};

	private static final double[] testModel =
			       {0, 0, 1,
					0, 1, 0,
					0, 0, 0,
					0, 0, 0,
					1};
	@Test
	public void testActions() {
		NimLineState state = new NimLineState(new int[] {1, 2, 2});
		Assertions.assertEquals(5, state.getActs().count());

		state = new NimLineState(new int[] {1, 3, 5, 7});
		Assertions.assertEquals(16, state.getActs().count());
	}

	@Test
	public void testToModel() {
		NimLineState state = new NimLineState(new int[] {1, 3, 5, 7});
		Assertions.assertArrayEquals(state.toModel(), standartModel);
		state = new NimLineState(new int[] {1, 2});
		Assertions.assertArrayEquals(state.toModel(), testModel);
	}
}
