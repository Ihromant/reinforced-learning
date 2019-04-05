package ua.ihromant.learning;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TicTacToeState implements State {
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

    public TicTacToeState(TicTacToeState prev, int nextMove, Player pl) {
        this(prev);
        if (players[nextMove] != null) {
            throw new IllegalArgumentException();
        }
        players[nextMove] = pl;
    }

    @Override
    public Collection<State> getActions() {
        List<Integer> notAssigned = IntStream.range(0, 9)
                .filter(i -> players[i] == null)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(notAssigned);
        return notAssigned.stream().map(i -> {
            TicTacToeState next = new TicTacToeState(this);
            next.players[i] = notAssigned.size() % 2 == 1 ? Player.X : Player.O;
            return next;
        }).collect(Collectors.toList());
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
    public double getUtility() {
        Player won = won();
        if (won == null) {
            return 0;
        }

        return won == Player.X ? -1 : 1;
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

    public enum Player {
        X, O
    }
}
