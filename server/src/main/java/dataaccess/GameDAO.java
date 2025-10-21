package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    void updateGame(GameData game, ChessGame updatedGame, String color, String username) throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;

    Collection<GameData> listGames();

    //void deleteGame(GameData game) throws DataAccessException;

    void deleteAllGames();
}
