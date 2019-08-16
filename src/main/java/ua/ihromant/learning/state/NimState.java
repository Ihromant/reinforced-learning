package ua.ihromant.learning.state;

public interface NimState extends State<NimAction> {
	int[] getPiles();
}
