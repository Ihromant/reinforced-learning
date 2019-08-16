package ua.ihromant.learning.state;

public interface TicTacToeState extends State<TTTAction> {
	Player getPlayer(int position);
}
