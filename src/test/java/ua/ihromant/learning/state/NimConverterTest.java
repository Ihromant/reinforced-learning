package ua.ihromant.learning.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.ihromant.learning.ai.qtable.converter.InputConverter;
import ua.ihromant.learning.ai.qtable.converter.NimStateConverter;

public class NimConverterTest {
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

	private InputConverter<NimState> converter = new NimStateConverter();

	@Test
	public void testToModel() {
		NimLineState state = new NimLineState(new int[] {1, 3, 5, 7});
		Assertions.assertArrayEquals(converter.convert(state), standartModel);
		state = new NimLineState(new int[] {1, 2});
		Assertions.assertArrayEquals(converter.convert(state), testModel);
	}
}
