package ua.ihromant.reinforced.ai;

import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.reinforced.ai.factory.TrainerFactory;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + "test.ai";
        TrainerFactory.newTicTacToeAgent(TicTacToeState3x3::new).train(100_000, path);
    }
}
