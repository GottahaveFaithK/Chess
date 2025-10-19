package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;

import java.util.UUID;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) {
        try {
            userDAO.createUser(user);
        } catch (DataAccessException e) {
            throw new ResponseException("Error: already taken", 403);
        }
        model.AuthData authData = new AuthData(user.username(), generateToken());
        authDAO.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) {
        try {
            UserData myUser = userDAO.getUser(user.username());
            if (!user.password().equals(myUser.password())) {
                throw new ResponseException("Error: unauthorized", 401);
            }
            AuthData authData = new AuthData(user.username(), generateToken());
            authDAO.createAuth(authData);
            return authData;

        } catch (DataAccessException e) {
            throw new ResponseException("Error: unauthorized", 401);
        }
    }

    public boolean logout(String authToken) {
        try {
            authDAO.getAuth(authToken);
            authDAO.deleteAuth(authToken);
            return true;
        } catch (DataAccessException e) {
            throw new ResponseException("Error: unauthorized", 401);
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
