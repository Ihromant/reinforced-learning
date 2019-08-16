package ua.ihromant.learning.factory;

import ua.ihromant.learning.GameBoard;
import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.state.State;

import java.util.function.Supplier;

public interface Factory<A> {
    Supplier<State<A>> getStateSupplier();

    Agent<A> player();

    Agent<A> createAI(String path);

    default GameBoard<A> createBoard(String path) {
        return new GameBoard<>(createAI(path), player(), getStateSupplier());
    }
}
