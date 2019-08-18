package ua.ihromant.reinforced.ai;

import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.learning.state.TicTacToeState5x5;
import ua.ihromant.learning.state.TicTacToeState5x6;
import ua.ihromant.reinforced.ai.factory.TrainerFactory;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + "5x5d.ai";
        String pathDist = System.getProperty("java.io.tmpdir") + File.separator + "5x5e.ai";
        String path3x3 = System.getProperty("java.io.tmpdir") + File.separator + "test2.ai";
        String path3x31 = System.getProperty("java.io.tmpdir") + File.separator + "test3.ai";
        String path5x6 = System.getProperty("java.io.tmpdir") + File.separator + "5x6.ai";
        String path5x61 = System.getProperty("java.io.tmpdir") + File.separator + "5x61.ai";
        //TrainerFactory.loadTicTacToeAgent(TicTacToeState5x5::new, path).train(100_000, pathDist);
        TrainerFactory.loadTicTacToeAgent(TicTacToeState5x6::new, path5x6).train(1_000_000, path3x31);
    }
}
