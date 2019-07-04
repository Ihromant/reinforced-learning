package ua.ihromant.learning;

import java.util.Scanner;

public class Main {
    private static AITemplate template = new MinimaxTemplate(Player.O);
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        TicTacToeState state = new TicTacToeState();
        while (!state.isTerminal()) {
            System.out.println("Enter your move, 1-9");
            System.out.println("123\n456\n789");
            int next = Integer.parseInt(scan.nextLine()) - 1;
            state = new TicTacToeState(state, next, Player.X);
            System.out.println(state.toString());
            if (state.isTerminal()) {
                break;
            }
            state = (TicTacToeState) template.decision(state).getTo();
            System.out.println(state.toString());
        }
        switch ((int) state.getUtility(Player.X)) {
            case 0:
                System.out.println("Draw!");
                return;
            case 1:
                System.out.println("You won!");
                return;
            case -1:
                System.out.println("Computer won!");
        }
    }
}
