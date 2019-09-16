package ua.ihromant.learning.state;

public interface Result {
    double getUtility(Player player);

    GameResult getGameResult(Player player);
}
