package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NimMultipleStateTest {
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
		NimMultipleState state = new NimMultipleState(new int[] {1, 2, 2});
		Assertions.assertEquals(10, state.getActs().count());

		state = new NimMultipleState(new int[] {1, 3, 5, 7});
		Assertions.assertEquals(37, state.getActs().count());
	}

	@Test
	public void testToModel() {
		NimMultipleState state = new NimMultipleState(new int[] {1, 3, 5, 7});
		Assertions.assertArrayEquals(state.toModel(), standartModel);
		state = new NimMultipleState(new int[] {1, 2});
		Assertions.assertArrayEquals(state.toModel(), testModel);
	}
}
