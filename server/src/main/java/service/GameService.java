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
                if (e.getMessage().contains("Game name is null")) {
                    throw new ResponseException("Error: bad request", 400);
                } else {
                    throw new ResponseException("Error: " + e.getMessage(), 500);
                }
            }
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Auth token doesn't exist")) {
                throw new ResponseException("Error: unauthorized", 401);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        try {
            authDAO.getAuth(authToken);
            try {
                return gameDAO.listGames();
            } catch (DataAccessException e) {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Auth token doesn't exist")) {
                throw new ResponseException("Error: unauthorized", 401);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
    }

    public void joinGame(String playerColor, int gameID, String authToken) {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Auth token doesn't exist")) {
                throw new ResponseException("Error: unauthorized", 401);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
        try {
            GameData myGame = gameDAO.getGame(gameID);
            try {
                gameDAO.updateColor(myGame, myGame.game(), playerColor, authDAO.getAuth(authToken).username());
            } catch (DataAccessException e) {
                if (e.getMessage().contains("Color already taken")) {
                    throw new ResponseException("Error: already taken", 403);
                } else if (e.getMessage().contains("Invalid color")) {
                    throw new ResponseException("Error: bad request", 400);
                } else {
                    throw new ResponseException("Error: " + e.getMessage(), 500);
                }
            }
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Game doesn't exist")) {
                throw new ResponseException("Error: bad request", 400);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
    }
}
