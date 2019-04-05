package ua.ihromant.learning;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        TicTacToeState state = new TicTacToeState();
        while (!state.isTerminal()) {
            System.out.println("Enter your move, 1-9");
            System.out.println("123\n456\n789");
            int next = Integer.parseInt(scan.nextLine()) - 1;
            state = new TicTacToeState(state, next, TicTacToeState.Player.X);
            System.out.println(state.toString());
            if (state.isTerminal()) {
                break;
            }
            state = (TicTacToeState) MinimaxTemplate.minimaxDecision(state);
            System.out.println(state.toString());
        }
        switch ((int) state.getUtility()) {
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
