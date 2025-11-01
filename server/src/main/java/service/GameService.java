package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(String gameName, String authToken) {
        try {
            authDAO.getAuth(authToken);
            try {
                return gameDAO.createGame(gameName);
            } catch (DataAccessException e) {
                throw new ResponseException("Error: bad request", 400);
            }
        } catch (DataAccessException e) {
            throw new ResponseException("Error: unauthorized", 401);
        }
    }

    public Collection<GameData> listGames(String authToken) {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException("Error: unauthorized", 401);
        }
        return gameDAO.listGames();
    }

    public void joinGame(String playerColor, int gameID, String authToken) {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException("Error: unauthorized", 401);
        }
        try {
            GameData myGame = gameDAO.getGame(gameID);
            if (playerColor.equals("WHITE") || playerColor.equals("BLACK")) {
                try {
                    gameDAO.updateColor(myGame, myGame.game(), playerColor, authDAO.getAuth(authToken).username());
                } catch (DataAccessException e) {
                    throw new ResponseException("Error: already taken", 403);
                }
            } else {
                throw new ResponseException("Error: this is where I break", 400);
            }
        } catch (DataAccessException e) {
            throw new ResponseException("Error: bad request", 400);
        }
    }
}
