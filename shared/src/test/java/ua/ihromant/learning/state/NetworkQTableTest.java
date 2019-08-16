package ua.ihromant.learning.state;

public class NetworkQTableTest {
	//private NetworkQTable<TTTAction> net = new NetworkQTable<>();

//	@Test
//	public void testQNetworkBatch() {
//		State<TTTAction> baseState = new TicTacToeState3x3();
//		TTTAction act = baseState.getActs().toArray(TTTAction[]::new)[0];
//		State<TTTAction> nextState = baseState.apply(act);
//		TTTAction next = nextState.getActs().toArray(TTTAction[]::new)[0];
//		State<TTTAction> nextNextState = nextState.apply(next);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//		net.set(nextState, 0.3);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//		net.set(nextNextState, -0.4);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//		net.set(nextState, 0.5);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//		net.set(nextNextState, -0.4);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//		net.set(nextState, -0.5);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//		net.set(nextNextState, 0.4);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//		net.set(nextState, -0.5);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//		net.set(nextNextState, 0.4);
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//	}
//
//	@Test
//	public void testQNetworkMultiple() {
//		State<TTTAction> baseState = new TicTacToeState3x3();
//		TTTAction act = baseState.getActs().toArray(TTTAction[]::new)[0];
//		State<TTTAction> nextState = baseState.apply(act);
//		TTTAction next = nextState.getActs().toArray(TTTAction[]::new)[0];
//		State<TTTAction> nextNextState = nextState.apply(next);
//		for (int i = 0; i < 200; i++) {
//			net.set(nextState, 0.3);
//			net.set(nextNextState, -0.4);
//			System.out.println(i + " " + Arrays.toString(net.getMultiple(Arrays.asList(nextState, nextNextState))));
//		}
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//	}
//
//	@Test
//	public void testQNetworkMultiple1() {
//		State<TTTAction> baseState = new TicTacToeState3x3();
//		TTTAction act = baseState.getActs().toArray(TTTAction[]::new)[0];
//		State<TTTAction> nextState = baseState.apply(act);
//		TTTAction next = nextState.getActs().toArray(TTTAction[]::new)[0];
//		State<TTTAction> nextNextState = nextState.apply(next);
//		Map<State<TTTAction>, Double> valuesMap = new HashMap<>();
//		valuesMap.put(nextState, 0.3);
//		valuesMap.put(nextNextState, -0.4);
//		for (int i = 0; i < 200; i++) {
//			net.setMultiple(valuesMap);
//			System.out.println(i + " " + Arrays.toString(net.getMultiple(Arrays.asList(nextState, nextNextState))));
//		}
//		System.out.println(Arrays.toString(net.getMultiple((Arrays.asList(nextState, nextNextState)))));
//	}
}
