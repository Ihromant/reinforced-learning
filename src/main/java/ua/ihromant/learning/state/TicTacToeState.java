package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeState implements State<TTTAction> {
    private static final int[][] WIN_COMBINATIONS = {
            {1, 2, 3},
            {1, 4, 7},
            {1, 5, 9},
            {2, 5, 8},
            {3, 6, 9},
            {3, 5, 7},
            {4, 5, 6},
            {7, 8, 9}
    };

    private final Player[] players = new Player[9];

    public TicTacToeState() {
    }

    private TicTacToeState(TicTacToeState prev) {
        System.arraycopy(prev.players, 0, this.players, 0, players.length);
    }

    private TicTacToeState(TicTacToeState prev, int nextMove) {
        this(prev);
        if (players[nextMove] != null) {
            throw new IllegalArgumentException();
        }
        players[nextMove] = getCurrent();
    }

    @Override
    public Player getCurrent() {
        return IntStream.range(0, 9)
                .filter(i -> players[i] == null)
                .count() % 2 == 1 ? Player.X : Player.O;
    }

    @Override
    public double[] toModel() {
        double[] result = new double[3 * 9];
        IntStream.range(0, 9)
                .forEach(i -> {
                    if (players[i] == Player.X) {
                        result[i] = 1;
                    }
                    if (players[i] == Player.O) {
                        result[i + 9] = 1;
                    }
                    if (players[i] == null) {
                        result[i + 18] = 1;
                    }
                });
        return result;
    }

    @Override
    public Stream<TTTAction> getActs() {
        if (isTerminal()) {
            return Stream.empty();
        }

        return IntStream.range(0, players.length)
                .filter(i -> players[i] == null)
                .mapToObj(TTTAction::new);
    }

    @Override
    public State<TTTAction> apply(TTTAction action) {
        return new TicTacToeState(this, action.getCoordinate());
    }

    private Player won() {
        return Arrays.stream(WIN_COMBINATIONS)
                .filter(arr -> players[arr[0] - 1] != null && players[arr[0] - 1] == players[arr[1] - 1] && players[arr[2] - 1] == players[arr[1] - 1])
                .map(arr -> players[arr[0] - 1]).findFirst().orElse(null);
    }

    @Override
    public boolean isTerminal() {
        return Arrays.stream(players).filter(Objects::nonNull).count() == 9 || won() != null;
    }

    @Override
    public GameResult getUtility(Player player) {
        Player won = won();
        if (won == null) {
            return GameResult.DRAW;
        }

        return player == won ? GameResult.WIN : GameResult.LOSE;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int coef = i * 3 + j;
                if (players[coef] != null) {
                    builder.append(players[coef].toString());
                } else {
                    builder.append(' ');
                }
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TicTacToeState that = (TicTacToeState) o;
        return Arrays.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(players);
    }
}
