package ua.ihromant.learning.state;

public enum GameResult {
	LOSE(0.0), DRAW(0.5), WIN(1.0);
	private final double val;

	GameResult(double val) {
		this.val = val;
	}

	public double toDouble() {
		return val;
	}
}
