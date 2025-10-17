package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void createGame(GameData game);

    void updateGame(GameData game);

    GameData getGame(int id);

    List<GameData> listGames();

    void deleteGame(GameData game);

    void deleteAllGames();
}
