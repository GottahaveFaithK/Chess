package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    @Test
    void clearAll() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        GameService gameService = new GameService(gameDAO, authDAO);
        gameService.createGame("myGame", res.authToken());
        ClearService clearService = new ClearService(gameDAO, userDAO, authDAO);
        clearService.clear();
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(user));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

}
