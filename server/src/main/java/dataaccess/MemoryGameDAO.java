package dataaccess;


import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> gameStorage = new HashMap<>();
    private int lastUsedID = 0;

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName != null && !gameName.isBlank()) {
            int newID = lastUsedID + 1;
            GameData gameData = new GameData(newID, null, null, gameName, new ChessGame());
            gameStorage.put(newID, gameData);
            lastUsedID = newID;
            return newID;
        } else {
            throw new DataAccessException("Game name is null");
        }
    }

    @Override
    public void updateGame(int gameID, ChessGame updatedGame) {
        GameData current = getGame(gameID);
        if (current != null) {
            gameStorage.put(gameID, new GameData(
                    gameID,
                    current.whiteUsername(),
                    current.blackUsername(),
                    current.gameName(),
                    updatedGame)
            );
        }
    }

    @Override
    public GameData getGame(int id) {
        return gameStorage.get(id);
    }

    @Override
    public Collection<GameData> listGames() {
        return gameStorage.values();
    }

    @Override
    public void deleteGame(GameData game) {
        gameStorage.remove(game.gameID());
    }

    @Override
    public void deleteAllGames() {
        gameStorage.clear();
    }
}
