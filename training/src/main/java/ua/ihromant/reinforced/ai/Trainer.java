package ua.ihromant.reinforced.ai;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.learning.state.TicTacToeState5x6;
import ua.ihromant.reinforced.ai.factory.TrainerFactory;

import java.io.File;

public class Trainer {
    static {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + "5x6b.ai";
        String next = System.getProperty("java.io.tmpdir") + File.separator + "5x6c.ai";
        TrainerFactory.loadTicTacToeAgent(TicTacToeState5x6::new, path).train(2_000_000, next);
    }
}
