package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.ihromant.learning.ai.converter.InputConverter;
import ua.ihromant.learning.ai.converter.NimStateConverter;
import ua.ihromant.learning.qtable.StateAction;

public class NimConverterTest {
	private static final double[] standartModel =
			       {0, 0, 1,
					0, 1, 1,
					1, 0, 1,
					1, 1, 1,
					0, 0, 0, 1,
					0, 1, 0,
					1};

	private static final double[] testModel =
			       {0, 0, 1,
					0, 1, 0,
					0, 0, 0,
					0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1,
					1};

	private final InputConverter<NimAction> converter = new NimStateConverter();

	@Test
	public void testToModel() {
		NimLineState state = new NimLineState(new int[] {1, 3, 5, 7});
		Assertions.assertArrayEquals(converter.convert(new StateAction<>(state, new NimAction(3, 2))), standartModel);
		state = new NimLineState(new int[] {1, 2});
		Assertions.assertArrayEquals(converter.convert(new StateAction<>(state, new NimAction(1, 1))), testModel);
	}
}
