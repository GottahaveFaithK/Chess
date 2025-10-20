package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {

    @Test
    void createGamePositive() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        GameService gameService = new GameService(gameDAO, authDAO);
        var response = gameService.createGame("myGame", res.authToken());
        assertEquals(1, response);
        var newRes = gameService.createGame("myGame", res.authToken());
        assertEquals(2, newRes);
    }
}
