package dataaccess;

import model.UserData;


public interface DataAccess {
    void saveUser(UserData user);

    void getUser(String username);
}
