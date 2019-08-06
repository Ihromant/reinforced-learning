package ua.ihromant.learning;

import ua.ihromant.learning.factory.Factory;
import ua.ihromant.learning.factory.TicTacToeSizedFactory;

public class Main {
    private static final Factory<?> factory = new TicTacToeSizedFactory();
    public static void main(String[] args) {
        factory.createBoard().play();
    }
}
