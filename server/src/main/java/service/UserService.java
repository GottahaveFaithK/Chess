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
            throw new ResponseException("Error: already taken", 403);
        }
        model.AuthData authData = new AuthData(user.username(), generateToken());
        try {
            authDAO.createAuth(authData);
        } catch (DataAccessException e) {
            throw new ResponseException("Error: Duplicate Auth", 403);
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
            throw new ResponseException("Error: unauthorized", 401);
        }
    }

    public void logout(String authToken) {
        try {
            authDAO.getAuth(authToken);
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException("Error: unauthorized", 401);
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
