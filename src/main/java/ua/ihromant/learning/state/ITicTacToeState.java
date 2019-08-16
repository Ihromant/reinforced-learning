package ua.ihromant.learning.state;

public interface ITicTacToeState extends State<TTTAction> {
	Player getPlayer(int position);
}
