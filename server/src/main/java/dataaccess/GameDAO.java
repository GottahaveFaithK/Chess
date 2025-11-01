package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    void updateGame(int id, GameData updatedData) throws DataAccessException;

    void updateColor(GameData game, ChessGame updatedGame, String color, String username) throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    //void deleteGame(GameData game) throws DataAccessException;

    void deleteAllGames() throws DataAccessException;
}
