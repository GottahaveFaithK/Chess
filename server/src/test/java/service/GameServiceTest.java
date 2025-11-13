package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.RegisterRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {

    @Test
    void createGamePositive() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGame = new CreateGameRequest(res.authToken(), "myGame");
        var response = gameService.createGame(createGame);
        assertEquals(1, response);
        var newRes = gameService.createGame(createGame);
        assertEquals(2, newRes);
    }

    @Test
    void createGameNoName() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGame = new CreateGameRequest(res.authToken(), "");
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.createGame(createGame));
        assertEquals(400, ex.getHttpResponseCode());
        assertEquals("Error: bad request", ex.getMessage());
    }

    @Test
    void createGameNoAuth() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        userService.register(registerRequest);
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGame = new CreateGameRequest("", "Game");
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.createGame(createGame));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    void joinGamePositive() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGame = new CreateGameRequest(res.authToken(), "game");
        int myGame = gameService.createGame(createGame);
        JoinGameRequest joinGameRequest = new JoinGameRequest(res.authToken(), "WHITE", myGame);
        gameService.joinGame(joinGameRequest);
    }

    @Test
    void joinGameIncorrectID() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        GameService gameService = new GameService(gameDAO, authDAO);
        JoinGameRequest joinGameRequest = new JoinGameRequest(res.authToken(), "WHITE", 234);
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.joinGame(joinGameRequest));
        assertEquals(400, ex.getHttpResponseCode());
        assertEquals("Error: bad request", ex.getMessage());
    }

    @Test
    void joinGameColorTaken() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        RegisterRequest registerRequest2 =
                new RegisterRequest("John", "eeee", "eeeee@jmail.com");
        var res = userService.register(registerRequest);
        var res2 = userService.register(registerRequest2);
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGame = new CreateGameRequest(res.authToken(), "game");
        int myGame = gameService.createGame(createGame);
        JoinGameRequest joinGameRequest = new JoinGameRequest(res.authToken(), "WHITE", myGame);
        gameService.joinGame(joinGameRequest);
        JoinGameRequest joinGameRequest2 = new JoinGameRequest(res2.authToken(), "WHITE", myGame);
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.joinGame(joinGameRequest2));
        assertEquals(403, ex.getHttpResponseCode());
        assertEquals("Error: already taken", ex.getMessage());
    }

    @Test
    void listGamesPositive() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGame = new CreateGameRequest(res.authToken(), "myGame");
        gameService.createGame(createGame);
        CreateGameRequest createOther = new CreateGameRequest(res.authToken(), "otherGame");
        gameService.createGame(createOther);
        ListGamesRequest listGames = new ListGamesRequest(res.authToken());
        var games = gameService.listGames(listGames);
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNoAuth() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGame = new CreateGameRequest(res.authToken(), "myGame");
        gameService.createGame(createGame);
        ListGamesRequest listGames = new ListGamesRequest("");
        ResponseException ex = assertThrows(ResponseException.class, ()
                -> gameService.listGames(listGames));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

}
