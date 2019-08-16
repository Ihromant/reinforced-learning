package ua.ihromant.learning.state;

public class TTTAction {
    private final int coordinate;

    public TTTAction(int coordinate) {
        this.coordinate = coordinate;
    }

    public int getCoordinate() {
        return coordinate;
    }
}
