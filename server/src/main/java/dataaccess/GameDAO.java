package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    void updateGame(int gameID, ChessGame updatedGame);

    GameData getGame(int id);

    Collection<GameData> listGames();

    void deleteGame(GameData game);

    void deleteAllGames();
}
