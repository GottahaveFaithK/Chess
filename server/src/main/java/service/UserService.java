package service;

import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;

public class UserService {
    private UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) {
        dataAccess.createUser(user);
        return new AuthData(user.username(), "zzyz"); //fix the zzyz with actual authtoken
    }
}
