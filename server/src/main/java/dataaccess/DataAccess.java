package dataaccess;

import datamodel.UserData;
import datamodel.UserData;


public interface DataAccess {
    void saveUser(UserData user);

    void getUser(String username);
}
