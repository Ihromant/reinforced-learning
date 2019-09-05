package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NimLineStateTest {
	@Test
	public void testActions() {
		NimLineState state = new NimLineState(new int[] {1, 2, 2});
		Assertions.assertEquals(5, state.getActions().count());

		state = new NimLineState(new int[] {1, 3, 5, 7});
		Assertions.assertEquals(16, state.getActions().count());
	}
}
