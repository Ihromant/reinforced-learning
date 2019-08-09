package ua.ihromant.learning;

import ua.ihromant.learning.factory.Factory;
import ua.ihromant.learning.factory.NimFactory;

public class Main {
    private static final Factory<?> factory = new NimFactory();
    public static void main(String[] args) {
        factory.createBoard().play();
    }
}
