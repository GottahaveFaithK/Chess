package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

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
}
