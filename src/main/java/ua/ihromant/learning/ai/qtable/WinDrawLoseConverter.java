package ua.ihromant.learning.ai.qtable;

public class WinDrawLoseConverter implements NeuralNetworkConverter {
	private double[] WIN = {1, 0, 0};
	private double[] LOSE = {0, 0, 1};
	private double[] DRAW = {0, 1, 0};
	@Override
	public double convertToQValue(double[] values) {
		return values[0] + values[1] * 0.5;
	}

	@Override
	public double[] fromQValue(double value) {
		if (value <= 0) {
			return LOSE;
		}
		if (value >= 1) {
			return WIN;
		}
		if (value == 0.5) {
			return DRAW;
		}
		if (value < 0.5) {
			return new double[] {0, 2 * value, 1 - 2 * value};
		}
		if (value > 0.5) {
			return new double[] {2 * value - 1, 2 - 2 * value, 0};
		}
		return DRAW; // never, but in case of NAN etc.
	}

	@Override
	public int length() {
		return 3;
	}
}
