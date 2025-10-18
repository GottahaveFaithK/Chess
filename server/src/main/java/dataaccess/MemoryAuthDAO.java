package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> authStorage = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authStorage.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authStorage.get(authToken);
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
