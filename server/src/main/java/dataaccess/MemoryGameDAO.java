package dataaccess;


import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> gameStorage = new HashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (game.gameName() != null) {
            gameStorage.put(game.gameID(), game);
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
                    updatedGame));
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
