package ua.ihromant.learning;

import ua.ihromant.learning.factory.Factory;
import ua.ihromant.learning.factory.NimLineFactory;
import ua.ihromant.learning.factory.TicTacToe5x5Factory;
import ua.ihromant.learning.factory.TicTacToe5x6Factory;
import ua.ihromant.learning.factory.TicTacToeFactory;

public class Main {
    private static final Factory<?> factory = new NimLineFactory();
    public static void main(String[] args) {
        factory.createBoard().play();
    }
}
