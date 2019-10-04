package ua.ihromant.learning.ai.converter;

import ua.ihromant.learning.qtable.StateAction;

public interface InputConverter<A> {
	double[] convert(StateAction<A> stateAction);

	StateAction<A> reverse(double[] from);

	int inputLength();
}
