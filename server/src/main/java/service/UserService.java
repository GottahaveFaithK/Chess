package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) {
        userDAO.createUser(user);
        return new AuthData(user.username(), "zzyz"); //fix the zzyz with actual authtoken
    }
}
