package ua.ihromant.learning.assumptions;

//import ua.ihromant.learning.state.State;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//import java.util.stream.Stream;
//
//public class MapNetworkBackedTable<A> implements MonteCarloSearchThree<A> {
//	private final Map<State<A>, Double> qStates = new HashMap<>();
//	private final QTable<A> backed;
//
//	public MapNetworkBackedTable(QTable<A> backed) {
//		this.backed = backed;
//	}
//
//	@Override
//	public double get(State<A> state) {
//		return qStates.getOrDefault(state, backed.get(state));
//	}
//
//	@Override
//	public Map<State<A>, Double> getMultiple(Stream<State<A>> stream) {
//		List<State<A>> states = stream.collect(Collectors.toList());
//		Map<State<A>, Double> result = new HashMap<>();
//		for (State<A> state : states) {
//			result.put(state, qStates.get(state));
//		}
//		int[] missing = IntStream.range(0, states.size()).filter(i -> result.get(states.get(i)) == null).toArray();
//		if (missing.length != 0) {
//			Map<State<A>, Double> computed = backed.getMultiple(Arrays.stream(missing).mapToObj(states::get));
//			IntStream.range(0, missing.length).forEach(i -> {
//				int missingIdx = missing[i];
//				qStates.put(states.get(missingIdx), computed.get(states.get(missingIdx)));
//				result.put(states.get(missingIdx), computed.get(states.get(missingIdx)));
//			});
//		}
//		return result;
//	}
//
//	@Override
//	public void set(State<A> state, double newValue) {
//		qStates.put(state, newValue);
//	}
//
//	@Override
//	public void setMultiple(Map<State<A>, Double> newValues) {
//		qStates.putAll(newValues);
//	}
//
//	@Override
//	public void serialize(String path) {
//		// TODO
//	}
//
//	@Override
//	public Map<State<A>, Double> getTree() {
//		return qStates;
//	}
//}
