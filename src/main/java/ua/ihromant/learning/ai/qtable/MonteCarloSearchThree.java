package ua.ihromant.learning.ai.qtable;

import java.util.Map;

import ua.ihromant.learning.state.Action;

public interface MonteCarloSearchThree extends QTable {
	Map<Action, Double> getTree();
}
