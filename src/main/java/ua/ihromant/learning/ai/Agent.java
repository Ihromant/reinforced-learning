package ua.ihromant.learning.ai;

import ua.ihromant.learning.state.State;

public interface Agent<S extends State> {
	S decision(S state);
}
