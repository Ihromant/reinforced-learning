package ua.ihromant.learning;

import java.util.Scanner;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.TicTacToePlayer;
import ua.ihromant.learning.agent.TicTacToeSizedPlayer;
import ua.ihromant.learning.ai.MinimaxTemplate;
import ua.ihromant.learning.ai.QLearningTemplate;
import ua.ihromant.learning.ai.qtable.MapQTable;
import ua.ihromant.learning.ai.qtable.NetworkQTable;
import ua.ihromant.learning.ai.qtable.WinDrawLoseConverter;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState;
import ua.ihromant.learning.state.TicTacToeStateSized;

public class Main {
    //private static Agent<TTTAction> ai = new MinimaxTemplate<>(Player.O);
    private static Agent<TTTAction> ai = new QLearningTemplate<>(new TicTacToeStateSized(),
            new NetworkQTable<>(new WinDrawLoseConverter()), 10000, 10);
    public static void main(String[] args) {
        new GameBoard<>(ai, new TicTacToeSizedPlayer(new Scanner(System.in)), TicTacToeStateSized::new).play();
    }
}
