package ua.ihromant.reinforced.ai;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;
import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.reinforced.ai.factory.TrainerFactory;
import ch.qos.logback.classic.Logger;

import java.io.File;

public class Trainer {
    static {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + "3x3.ai";
        TrainerFactory.newTicTacToeAgent(TicTacToeState3x3::new).train(100_000, path);
    }
}
