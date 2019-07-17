package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NimStateTest {
	@Test
	public void testActions() {
		NimState state = new NimState(new int[] {1, 2, 2});
		Assertions.assertEquals(10, state.getActions().count());

		state = new NimState(new int[] {1, 3, 5, 7});
		Assertions.assertEquals(37, state.getActions().count());
	}
}
