package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EGreedyPolicy<A> extends GreedyPolicy<A> {
    private final Random rand = new Random();
    private final double conservativeRate;
    public EGreedyPolicy(QTable qTable, double conservativeRate) {
        super(qTable);
        this.conservativeRate = conservativeRate;
    }

    @Override
    public State apply(Stream<State<A>> actionStream) {
        double d = rand.nextDouble();
        if (d < conservativeRate) {
            return super.apply(actionStream);
        }

        List<State<A>> actions = actionStream.collect(Collectors.toList());
        return actions.get(rand.nextInt(actions.size()));
    }
}
