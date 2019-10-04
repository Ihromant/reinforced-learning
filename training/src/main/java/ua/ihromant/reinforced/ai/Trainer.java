package ua.ihromant.reinforced.ai;

//import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import ua.ihromant.learning.state.NimLineState;
import ua.ihromant.learning.state.NimState;
import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.reinforced.ai.factory.TrainerFactory;
//import ch.qos.logback.classic.Logger;

import java.io.File;

public class Trainer {
    static {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + "nimMap.ai";
        TrainerFactory.newNimAgent(() -> new NimLineState(new int[] {1, 3, 5, 7})).train(20_000, path);
    }
}
