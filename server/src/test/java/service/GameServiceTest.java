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

    @Test
    void createGameNoName() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        GameService gameService = new GameService(gameDAO, authDAO);
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.createGame("", res.authToken()));
        assertEquals(400, ex.getHttpResponseCode());
        assertEquals("Error: bad request", ex.getMessage());
    }

    @Test
    void createGameNoAuth() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(user);
        GameService gameService = new GameService(gameDAO, authDAO);
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.createGame("Game", ""));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    void joinGamePositive() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        GameService gameService = new GameService(gameDAO, authDAO);
        int myGame = gameService.createGame("game", res.authToken());
        gameService.joinGame("WHITE", myGame, res.authToken());
    }

    @Test
    void joinGameIncorrectID() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        GameService gameService = new GameService(gameDAO, authDAO);
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.joinGame("WHITE", 234, res.authToken()));
        assertEquals(400, ex.getHttpResponseCode());
        assertEquals("Error: bad request", ex.getMessage());
    }

    @Test
    void joinGameColorTaken() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        var userTwo = new UserData("John", "eeee", "eeeee@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        var res2 = userService.register(userTwo);
        GameService gameService = new GameService(gameDAO, authDAO);
        int myGame = gameService.createGame("game", res.authToken());
        gameService.joinGame("WHITE", myGame, res.authToken());
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.joinGame("WHITE", myGame, res2.authToken()));
        assertEquals(403, ex.getHttpResponseCode());
        assertEquals("Error: already taken", ex.getMessage());
    }

}
