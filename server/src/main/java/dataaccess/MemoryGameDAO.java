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
    public void updateGame(GameData game, ChessGame updatedGame, String color, String username) throws DataAccessException {
        if (color.equals("WHITE") && game.whiteUsername() == null) {
            gameStorage.put(game.gameID(), new GameData(
                    game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    updatedGame)
            );
        } else if (color.equals("BLACK") && game.blackUsername() == null) {
            gameStorage.put(game.gameID(), new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    updatedGame)
            );
        } else {
            throw new DataAccessException("Color already taken");
        }
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        GameData myGame = gameStorage.get(id);
        if (myGame != null) {
            return myGame;
        } else {
            throw new DataAccessException("Game ID is invalid");
        }
    }

    @Override
    public Collection<GameData> listGames() {
        return gameStorage.values();
    }

    @Override
    public void deleteAllGames() {
        gameStorage.clear();
    }
}
