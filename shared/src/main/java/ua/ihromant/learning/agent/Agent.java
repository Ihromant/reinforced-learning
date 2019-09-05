package ua.ihromant.learning.agent;

import ua.ihromant.learning.state.State;

public interface Agent<A> {
	A decision(State<A> from);
}
