package ua.ihromant.reinforced.ai;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import ua.ihromant.learning.state.NimLineState;
import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.learning.state.TicTacToeState5x5;
import ua.ihromant.learning.state.TicTacToeState5x6;
import ua.ihromant.reinforced.ai.factory.TrainerFactory;

import java.io.File;

public class Trainer {
    static {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        String pathPrev = System.getProperty("java.io.tmpdir") + File.separator + "ttt.ai";
        String path = System.getProperty("java.io.tmpdir") + File.separator + "ttt1.ai";
        TrainerFactory.newTicTacToeAgent(25).train(new TicTacToeState5x5(), 1_000_000, pathPrev);
        //TrainerFactory.loadTicTacToeAgent(9, pathPrev).train(new TicTacToeState3x3(), 100_000, path);
    }
}
