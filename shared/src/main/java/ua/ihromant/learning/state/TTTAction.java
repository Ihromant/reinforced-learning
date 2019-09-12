package ua.ihromant.learning.state;

import java.io.Serializable;
import java.util.Objects;

public class TTTAction implements Serializable {
    private final int coordinate;

    public TTTAction(int coordinate) {
        this.coordinate = coordinate;
    }

    public int getCoordinate() {
        return coordinate;
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
}
