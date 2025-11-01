package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public ClearService(GameDAO gameDAO, UserDAO userDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clear() {
        try {
            authDAO.deleteAllAuth();
            userDAO.deleteAllUsers();
            gameDAO.deleteAllGames();
        } catch (DataAccessException e) {
            throw new ResponseException("Error: " + e.getMessage(), 500);
        }
    }

}
