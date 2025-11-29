package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;

import java.util.UUID;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(RegisterRequest request) {
        String hashPwd = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        var hashUser = new UserData(request.username(), hashPwd, request.email());
        try {
            userDAO.createUser(hashUser);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Username already taken")) {
                throw new ResponseException("Error: already taken", 403);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
        model.AuthData authData = new AuthData(request.username(), generateToken());
        try {
            authDAO.createAuth(authData);
        } catch (DataAccessException e) {
            throw new ResponseException("Error: " + e.getMessage(), 500);
        }
        return authData;
    }

    public AuthData login(LoginRequest request) {
        try {
            UserData myUser = userDAO.getUser(request.username());
            if (myUser == null || !BCrypt.checkpw(request.password(), myUser.password())) {
                throw new ResponseException("Error: unauthorized", 401);
            }
            AuthData authData = new AuthData(request.username(), generateToken());
            authDAO.createAuth(authData);
            return authData;

        } catch (DataAccessException e) {
            if (e.getMessage().contains("Invalid Username")) {
                throw new ResponseException("Error: unauthorized", 401);
            }
            throw new ResponseException("Error: " + e.getMessage(), 500);
        }
    }

    public void logout(LogoutRequest request) {
        try {
            String authToken = request.authToken();
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

    public boolean verify(String authToken) {
        try {
            authDAO.getAuth(authToken);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public String getUsername(String authToken) {
        try {
            AuthData auth = authDAO.getAuth(authToken);
            return auth.username();
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
