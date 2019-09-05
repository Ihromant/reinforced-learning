package ua.ihromant.learning.util;

import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.qtable.QTable;
import ua.ihromant.learning.qtable.StateAction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WriterUtil {
    public static <A> void writeHistory(List<HistoryItem<A>> history, QTable<A> qTable) {
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

    public static <A> void writeHistory(List<HistoryItem<A>> history) {
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
}
