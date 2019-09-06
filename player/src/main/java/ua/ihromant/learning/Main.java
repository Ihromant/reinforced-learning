package ua.ihromant.learning;

import ua.ihromant.learning.factory.Factory;
import ua.ihromant.learning.factory.TicTacToeFactory;

import java.io.File;

public class Main {
    private static final Factory<?> factory = new TicTacToeFactory();
    public static void main(String[] args) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + "test.ai";
        factory.createBoard(path).play();
    }
}
