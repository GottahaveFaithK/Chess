package dataaccess;


import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private HashMap<String, GameData> gameStorage = new HashMap<>();

    @Override
    public void createGame(GameData game) {
        //TODO impl this
    }

    @Override
    public void updateGame(GameData game) {
        //TODO impl this
    }

    @Override
    public GameData getGame(int id) {
        GameData test = new GameData(67, "white", "black", "hades",
                new ChessGame());
        return test;
        //TODO  test interface with this
    }

    @Override
    public List<GameData> listGames() {
        List<GameData> test = new ArrayList<>();
        return test;
        //TODO impl this
    }

    @Override
    public void deleteGame(GameData game) {
        //TODO impl this
    }

    @Override
    public void deleteAllGames() {
        //TODO impl this
    }
}
