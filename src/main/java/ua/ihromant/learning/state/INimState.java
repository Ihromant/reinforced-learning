package ua.ihromant.learning.state;

public interface INimState extends State<NimAction> {
	int[] getPiles();
}
