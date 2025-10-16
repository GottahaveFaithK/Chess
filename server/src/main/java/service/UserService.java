package service;

import dataaccess.DataAccess;
import datamodel.UserData;
import datamodel.AuthData;

public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) {
        dataAccess.saveUser(user);
        return new AuthData(user.username(), "zzyz"); //fix the zzyz with actual authtoken
    }
}
