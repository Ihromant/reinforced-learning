package ua.ihromant.learning;

import ua.ihromant.learning.factory.Factory;
import ua.ihromant.learning.factory.TicTacToe3x3Factory;
import ua.ihromant.learning.factory.TicTacToe5x5Factory;

import java.io.File;

public class Gamer {
    private static final Factory<?> factory = new TicTacToe5x5Factory();
    public static void main(String[] args) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + "5x5k1.ai";
        factory.createBoard(path).play();
    }
}
