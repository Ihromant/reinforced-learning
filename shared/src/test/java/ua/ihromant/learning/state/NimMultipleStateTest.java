package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NimMultipleStateTest {
	@Test
	public void testActions() {
		NimMultipleState state = new NimMultipleState(new int[] {1, 2, 2});
		Assertions.assertEquals(10, state.getActions().count());

		state = new NimMultipleState(new int[] {1, 3, 5, 7});
		Assertions.assertEquals(37, state.getActions().count());
	}
}
