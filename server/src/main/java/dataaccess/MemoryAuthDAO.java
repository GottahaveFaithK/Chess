package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authStorage = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authStorage.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authStorage.get(authToken) == null) {
            throw new DataAccessException("Auth token doesn't exist");
        } else {
            return authStorage.get(authToken);
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        authStorage.remove(authToken);
    }

    @Override
    public void deleteAllAuth() {
        authStorage.clear();
    }
}
