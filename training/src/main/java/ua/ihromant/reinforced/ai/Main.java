package ua.ihromant.reinforced.ai;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;
import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.learning.state.TicTacToeState5x5;
import ua.ihromant.learning.state.TicTacToeState5x6;
import ua.ihromant.reinforced.ai.factory.TrainerFactory;
import ch.qos.logback.classic.Logger;

import java.io.File;

public class Main {
    static {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + "5x5d.ai";
        String pathDist = System.getProperty("java.io.tmpdir") + File.separator + "5x5e.ai";
        String path3x3 = System.getProperty("java.io.tmpdir") + File.separator + "test2.ai";
        String path3x31 = System.getProperty("java.io.tmpdir") + File.separator + "test3.ai";
        String path5x6 = System.getProperty("java.io.tmpdir") + File.separator + "5x67.ai";
        String path5x61 = System.getProperty("java.io.tmpdir") + File.separator + "5x68.ai";
       // TrainerFactory.loadTicTacToeAgent(TicTacToeState5x6::new, path5x6).train(1_000_000, path5x61);
        //TrainerFactory.newTicTacToeAgent(TicTacToeState3x3::new).train(500_000, path3x3);
	    TrainerFactory.loadTicTacToeAgent(TicTacToeState3x3::new, path3x3).train(500_000, path3x31);
    }
}
