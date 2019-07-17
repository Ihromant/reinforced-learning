package ua.ihromant.learning.ai.qtable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ua.ihromant.learning.state.Action;

public class EGreedyPolicy extends GreedyPolicy {
    private final Random rand = new Random();
    private final double conservativeRate;
    public EGreedyPolicy(QTable qTable, double conservativeRate) {
        super(qTable);
        this.conservativeRate = conservativeRate;
    }

    @Override
    public Action apply(Stream<Action> actionStream) {
        double d = rand.nextDouble();
        if (d < conservativeRate) {
            return super.apply(actionStream);
        }

        List<Action> actions = actionStream.collect(Collectors.toList());
        return actions.get(rand.nextInt(actions.size()));
    }
}
