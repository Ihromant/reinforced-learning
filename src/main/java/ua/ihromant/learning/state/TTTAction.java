package ua.ihromant.learning.state;

public class TTTAction {
    private final Player player;
    private final int coordinate;

    public TTTAction(Player player, int coordinate) {
        this.player = player;
        this.coordinate = coordinate;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCoordinate() {
        return coordinate;
    }
}
