package ua.ihromant.learning;

import ua.ihromant.learning.ai.AITemplate;
import ua.ihromant.learning.ai.QLearningTemplate;
import ua.ihromant.learning.ai.qtable.MapQTable;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState;

public class Main {
    // private static AITemplate template = new MinimaxTemplate(Player.O);
    private static AITemplate<TTTAction> template = new QLearningTemplate<>(new TicTacToeState(),
            new MapQTable<>(0.3), 100000, 10);
    public static void main(String[] args) {
        new GameBoard(template).play();
    }
}
