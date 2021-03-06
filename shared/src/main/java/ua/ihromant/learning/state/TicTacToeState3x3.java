package ua.ihromant.learning.state;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeState3x3 implements TicTacToeState {
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

    public TicTacToeState3x3() {
    }

    private TicTacToeState3x3(TicTacToeState3x3 prev) {
        System.arraycopy(prev.players, 0, this.players, 0, players.length);
    }

    private TicTacToeState3x3(TicTacToeState3x3 prev, int nextMove) {
        this(prev);
        if (players[nextMove] != null) {
            throw new IllegalArgumentException();
        }
        players[nextMove] = getCurrent();
    }

    @Override
    public Player getPlayer(int position) {
        return players[position];
    }

    @Override
    public int horSize() {
        return 3;
    }

    @Override
    public int verSize() {
        return 3;
    }

    @Override
    public Player getCurrent() {
        return IntStream.range(0, 9)
                .filter(i -> players[i] == null)
                .count() % 2 == 1 ? Player.X : Player.O;
    }

    @Override
    public int getMaximumMoves() {
        return (int) IntStream.range(0, 9).filter(i -> players[i] == null).count();
    }

    @Override
    public Stream<TTTAction> getActions() {
        return IntStream.range(0, players.length)
                .filter(i -> players[i] == null)
                .mapToObj(TTTAction::of);
    }

    @Override
    public State<TTTAction> apply(TTTAction action) {
        return new TicTacToeState3x3(this, action.getCoordinate());
    }

    private Player won() {
        return Arrays.stream(WIN_COMBINATIONS)
                .filter(arr -> players[arr[0] - 1] != null && players[arr[0] - 1] == players[arr[1] - 1] && players[arr[2] - 1] == players[arr[1] - 1])
                .map(arr -> players[arr[0] - 1]).findFirst().orElse(null);
    }

    @Override
    public Result getResult() {
        long moves = Arrays.stream(players).filter(Objects::nonNull).count();
        Player won = won();
        if (won != null) {
            return new BoardResult(won, (int) moves, 5);
        }
        if (moves == players.length) {
            return new BoardResult();
        }
        return null;
    }

    @Override
    public String toString() {
        int SIZE = 3;
        StringBuilder builder = new StringBuilder();
        builder.append("-".repeat(SIZE + 2));
        builder.append('\n');
        for (int i = 0; i < SIZE; i++) {
            builder.append('|');
            for (int j = 0; j < SIZE; j++) {
                int coef = i * SIZE + j;
                if (players[coef] != null) {
                    builder.append(getPlayer(coef).toString());
                } else {
                    builder.append(' ');
                }
            }
            builder.append('|');
            builder.append('\n');
        }
        builder.append("-".repeat(SIZE + 2));
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
        TicTacToeState3x3 that = (TicTacToeState3x3) o;
        return Arrays.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(players);
    }

    @Override
    public GameResult getExpectedResult(Player pl) {
        return GameResult.DRAW;
    }
}
