package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Username already taken");
        } else {
            users.put(user.username(), user);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (users.get(username) == null) {
            throw new DataAccessException("Invalid Username");
        } else {
            return users.get(username);
        }
    }

    @Override
    public void deleteUser(UserData user) {
        users.remove(user.username());
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}
