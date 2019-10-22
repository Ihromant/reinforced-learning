package ua.ihromant.learning.state;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.IntStream;

public class TTTAction implements Serializable {
	private static final int CACHE_SIZE = 225;
	private static final TTTAction[] CACHE = IntStream.range(0, CACHE_SIZE).mapToObj(TTTAction::new).toArray(TTTAction[]::new);
    private final int coordinate;

    private TTTAction(int coordinate) {
        this.coordinate = coordinate;
    }

    public int getCoordinate() {
        return coordinate;
    }

    public static TTTAction of (int coordinate) {
    	if (coordinate >= 0 && coordinate < CACHE_SIZE) {
    		return CACHE[coordinate];
	    }

    	return new TTTAction(coordinate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TTTAction tttAction = (TTTAction) o;
        return coordinate == tttAction.coordinate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate);
    }

    @Override
    public String toString() {
        return "TTTAction{" +
                "coordinate=" + coordinate +
                '}';
    }
}
