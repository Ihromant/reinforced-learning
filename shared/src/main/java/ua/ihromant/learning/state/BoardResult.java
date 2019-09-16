package ua.ihromant.learning.state;

public class BoardResult implements Result {
    private final Player player;
    private final int moves;
    private final int baseShift;
    private final double step;

    public BoardResult(Player player, int moves, int baseShift, double step) {
        this.player = player;
        this.moves = moves;
        this.baseShift = baseShift;
        this.step = step;
    }

    public BoardResult(Player player, int moves, int baseShift) {
        this(player, moves, baseShift, 0.01);
    }

    public BoardResult(Player player) {
        this(player, 0, 0);
    }

    public BoardResult() {
        this(null);
    }

    public double getUtility(Player player) {
        if (this.player == null) {
            return GameResult.DRAW.toDouble();
        }

        int diff = moves - baseShift;
        if (this.player == player) {
            return GameResult.WIN.toDouble() - diff * step;
        } else {
            return GameResult.LOSE.toDouble() + diff * step;
        }
    }

    public GameResult getGameResult(Player player) {
        if (this.player == null) {
            return GameResult.DRAW;
        }

        return this.player == player ? GameResult.WIN : GameResult.LOSE;
    }
}
