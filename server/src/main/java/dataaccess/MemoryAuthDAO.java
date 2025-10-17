package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> authStorage = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        //TODO impl this
    }

    @Override
    public AuthData getAuth(String authToken) {
        AuthData test = new AuthData("cow", "xyz");
        return test;
        //TODO write test for this to test interface
    }

    @Override
    public void deleteAuth(AuthData authData) {
        //TODO impl this
    }

    @Override
    public void deleteAllAuth() {
        //TODO impl this
    }
}
