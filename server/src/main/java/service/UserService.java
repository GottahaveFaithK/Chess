package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) {
        String hashPwd = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var hashUser = new UserData(user.username(), hashPwd, user.email());
        try {
            userDAO.createUser(hashUser);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Username already taken")) {
                throw new ResponseException("Error: already taken", 403);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
        model.AuthData authData = new AuthData(user.username(), generateToken());
        try {
            authDAO.createAuth(authData);
        } catch (DataAccessException e) {
            throw new ResponseException("Error: " + e.getMessage(), 500);
        }
        return authData;
    }

    public AuthData login(UserData user) {
        try {
            UserData myUser = userDAO.getUser(user.username());
            if (myUser == null || !BCrypt.checkpw(user.password(), myUser.password())) {
                throw new ResponseException("Error: unauthorized", 401);
            }
            AuthData authData = new AuthData(user.username(), generateToken());
            authDAO.createAuth(authData);
            return authData;

        } catch (DataAccessException e) {
            if (e.getMessage().contains("Invalid Username")) {
                throw new ResponseException("Error: unauthorized", 401);
            }
            throw new ResponseException("Error: " + e.getMessage(), 500);
        }
    }

    public void logout(String authToken) {
        try {
            authDAO.getAuth(authToken);
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Auth token doesn't exist")) {
                throw new ResponseException("Error: unauthorized", 401);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
