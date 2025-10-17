package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> userStorage = new HashMap<>();

    @Override
    public void createUser(UserData user) {
        //TODO impl this
    }

    @Override
    public UserData getUser(String username) {
        UserData test = new UserData("cow", "password", "cow@gmail.com");
        return test;
        //TODO test this to see if interface is working
    }

    @Override
    public void deleteUser(UserData user) {
        //TODO impl this
    }

    @Override
    public void deleteAllUsers() {
        //TODO impl this
    }
}
