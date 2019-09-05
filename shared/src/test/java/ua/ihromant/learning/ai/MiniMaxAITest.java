package ua.ihromant.learning.ai;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.ihromant.learning.agent.GamePlayer;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.NimLineState;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TicTacToeState3x3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MiniMaxAITest {
    private int[] def = {1, 2, 3};
    private Map<Player, MinimaxAI> players = Arrays.stream(
            Player.values()).collect(Collectors.toMap(Function.identity(), MinimaxAI::new));
    @Test
    public void testTicTacToe() {
        IntStream.range(0, 10)
                .forEach(i -> {
                    System.out.println(i + " minimax test for tictactoe");
                    List<HistoryItem> items = new GamePlayer(players, new TicTacToeState3x3()).play();
                    Assertions.assertEquals(GameResult.DRAW, items.get(items.size() - 1).getTo().getUtility(Player.X));
                });
    }

    @Test
    public void testNim() {
        IntStream.range(0, 1000)
                .forEach(i -> {
                    System.out.println(i + " minimax test for nim");
                    List<HistoryItem> items = new GamePlayer(players, new NimLineState(def)).play();
                    Assertions.assertEquals(GameResult.LOSE, items.get(items.size() - 1).getTo().getUtility(Player.X));
                });
    }
}
