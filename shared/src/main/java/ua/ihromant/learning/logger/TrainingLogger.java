package ua.ihromant.learning.logger;

import org.nd4j.shade.jackson.databind.ObjectMapper;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.qtable.QTable;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.util.WriterUtil;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainingLogger<A> {
    private final Map<GameResult, Integer> statistics = new EnumMap<>(GameResult.class);
    private final List<List<HistoryItem<A>>> conservativeWrong = new ArrayList<>();
    private final List<int[]> stat = new ArrayList<>();
    private Map<StateAction<A>, Long> prev = new HashMap<>();
    private final int episodes;
    private final int step;
    private final long start;
    private long time;

    public TrainingLogger(int episodes, int step) {
        this.episodes = episodes;
        this.step = step;
        this.start = System.currentTimeMillis();
        this.time = start;
    }

    public void writeHistory(List<HistoryItem<A>> history, QTable<A> qTable, int episode) {
        GameResult result = history.get(history.size() - 1).getResult().getGameResult(Player.X);
        if (history.stream().noneMatch(HistoryItem::isRandom)) {
            if (result != history.get(0).getTo().getExpectedResult(Player.X)) {
                conservativeWrong.add(new ArrayList<>(history));
            }
        }
        statistics.put(result, statistics.get(result) == null ? 1 : statistics.get(result) + 1);
        if (episode % step == step - 1) {
            long nextTime = System.currentTimeMillis();
            System.out.println("Learning " + 100.0 * (episode + 1) / episodes + "% complete, elapsed: "
                    + (nextTime - time) + " ms, statistics for player X: " + statistics
                    + ", conservative wrong size: " + conservativeWrong.size());
            IntStream.range(0, Math.min(conservativeWrong.size(), 3))
                    .forEach(j -> WriterUtil.writeHistory(conservativeWrong.get(j), qTable));
            writeHistory(history, qTable);
            stat.add(new int[] {episode + 1, statistics.getOrDefault(GameResult.WIN, 0),
                    statistics.getOrDefault(GameResult.DRAW, 0),
                    statistics.getOrDefault(GameResult.LOSE, 0), conservativeWrong.size()});
            prev.forEach((k, v) -> System.out.print(k.getAction() + " -> " +  v + " "));
            System.out.println("Size: " + prev.size());
            prev = conservativeWrong.stream().collect(Collectors.groupingBy(l -> l.get(0).getStateAction(), Collectors.counting()));
            statistics.clear();
            conservativeWrong.clear();
            time = nextTime;
        }
        if (episode + 1 == episodes) {
            try {
                System.out.println("Extracted statictics: " + new ObjectMapper().writeValueAsString(stat));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    public void writeHistory(List<HistoryItem<A>> history, QTable<A> qTable) {
        List<String[]> lines = history.stream()
                .map(h -> h.getTo().toString())
                .map(s -> s.split("\n")).collect(Collectors.toList());
        Map<StateAction<A>, Double> evals = qTable.getMultiple(history.stream().map(HistoryItem::getStateAction));
        String[] firstLine = lines.get(0);
        for (int i = 0; i < history.size(); i++) {
            String format = history.get(i).isRandom()
                    ? "R%." + (lines.get(i)[0].length() - 2) + "f"
                    : "%." + (lines.get(i)[0].length() - 1) + "f";
            System.out.print(String.format(format, evals.get(history.get(i).getStateAction())) + " ");
        }
        System.out.println();
        for (int i = 0; i < firstLine.length; i++) {
            for (int j = 0; j < history.size(); j++) {
                System.out.print(lines.get(j)[i] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void writeHistory(List<HistoryItem<A>> history) {
        List<String[]> lines = history.stream()
                .map(h -> h.getTo().toString())
                .map(s -> s.split("\n")).collect(Collectors.toList());
        for (int i = 0; i < lines.get(0).length; i++) {
            for (int j = 0; j < history.size(); j++) {
                System.out.print(lines.get(j)[i] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public Map<StateAction<A>, Long> getPrev() {
        return prev;
    }
}
