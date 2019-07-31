package ua.ihromant.learning.ai;

import ua.ihromant.learning.state.State;

public interface AITemplate<A> {
	State<A> decision(State<A> state);
}
