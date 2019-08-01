package ua.ihromant.learning.ai;

import ua.ihromant.learning.state.Action;
import ua.ihromant.learning.state.State;

public interface AITemplate {
	Action decision(State state);
}
