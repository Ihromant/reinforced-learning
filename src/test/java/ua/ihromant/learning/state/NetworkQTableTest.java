package ua.ihromant.learning.state;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import ua.ihromant.learning.ai.qtable.NetworkQTable;

public class NetworkQTableTest {
	private NetworkQTable net = new NetworkQTable();

	@Test
	public void testQNetwork() {
		State baseState = new TicTacToeState();
		Action act = baseState.getActions().toArray(Action[]::new)[0];
		System.out.println(act);
		System.out.println(net.get(act));
		net.set(act, 0.3);
		System.out.println(net.get(act));
		net.set(act, 0.5);
		System.out.println(net.get(act));
		net.set(act, -0.5);
		System.out.println(net.get(act));
		net.set(act, -0.5);
		System.out.println(net.get(act));
	}

	@Test
	public void testQNetworkBatch() {
		State baseState = new TicTacToeState();
		Action act = baseState.getActions().toArray(Action[]::new)[0];
		Action next = act.getTo().getActions().toArray(Action[]::new)[0];
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
		net.set(act, 0.3);
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
		net.set(next, 0.4);
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
		net.set(act, 0.5);
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
		net.set(next, 0.4);
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
		net.set(act, -0.5);
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
		net.set(next, -0.4);
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
		net.set(act, -0.5);
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
		net.set(next, -0.4);
		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(act, next)))));
	}
}
