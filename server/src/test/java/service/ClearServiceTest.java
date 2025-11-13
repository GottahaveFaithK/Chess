package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;
import request.ClearDatabaseRequest;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    @Test
    void clearAll() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        ClearDatabaseRequest clearDatabaseRequest = new ClearDatabaseRequest();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGameRequest = new CreateGameRequest(res.authToken(), "myGame");
        gameService.createGame(createGameRequest);
        ClearService clearService = new ClearService(gameDAO, userDAO, authDAO);
        clearService.clear();
        LoginRequest loginRequest = new LoginRequest("joe", "j@j");
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(loginRequest));
        assertEquals(401, ex.getHttpResponseCode());
    }

}
