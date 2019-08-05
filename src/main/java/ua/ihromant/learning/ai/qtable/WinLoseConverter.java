package ua.ihromant.learning.ai.qtable;

public class WinLoseConverter implements NeuralNetworkConverter {
	private double[] WIN = {1, 0};
	private double[] LOSE = {0, 1};
	@Override
	public double convertToQValue(double[] values) {
		return values[0];
	}

	@Override
	public double[] fromQValue(double value) {
		if (value <= 0) {
			return LOSE;
		}
		if (value >= 1) {
			return WIN;
		}
		if (Double.isFinite(value)) {
			return new double[] {value, 1 - value};
		}

		return LOSE; // never, but in case of NAN etc.
	}


	@Override
	public int length() {
		return 2;
	}
}
