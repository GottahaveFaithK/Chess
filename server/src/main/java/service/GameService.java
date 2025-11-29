package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;

import java.util.Collection;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(CreateGameRequest request) {
        try {
            String gameName = request.gameName();
            String authToken = request.authToken();
            authDAO.getAuth(authToken);
            try {
                return gameDAO.createGame(gameName);
            } catch (DataAccessException e) {
                if (e.getMessage().contains("Game name is null")) {
                    throw new ResponseException("Error: bad request", 400);
                } else {
                    throw new ResponseException("Error: " + e.getMessage(), 500);
                }
            }
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Auth token doesn't exist")) {
                throw new ResponseException("Error: unauthorized", 401);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
    }

    public Collection<GameData> listGames(ListGamesRequest request) throws ResponseException {
        try {
            authDAO.getAuth(request.authToken());
            try {
                return gameDAO.listGames();
            } catch (DataAccessException e) {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Auth token doesn't exist")) {
                throw new ResponseException("Error: unauthorized", 401);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
    }

    public void joinGame(JoinGameRequest request) {
        AuthData auth;
        try {
            auth = authDAO.getAuth(request.authToken());
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Auth token doesn't exist")) {
                throw new ResponseException("Error: unauthorized", 401);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }

        try {
            GameData myGame = gameDAO.getGame(request.gameID());
            try {
                gameDAO.updateColor(myGame, myGame.game(), request.playerColor(),
                        auth.username());
            } catch (DataAccessException e) {
                if (e.getMessage().contains("Color already taken")) {
                    throw new ResponseException("Error: already taken", 403);
                } else if (e.getMessage().contains("Invalid color")) {
                    throw new ResponseException("Error: bad request", 400);
                } else {
                    throw new ResponseException("Error: " + e.getMessage(), 500);
                }
            }
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Game doesn't exist")) {
                throw new ResponseException("Error: bad request", 400);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
    }

    public String getPlayerColor(String authToken, int gameID) {
        String username;
        try {
            AuthData auth = authDAO.getAuth(authToken);
            username = auth.username();
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Auth token doesn't exist")) {
                throw new ResponseException("Error: unauthorized", 401);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }

        String playerColor;
        try {
            playerColor = gameDAO.getPlayerColor(gameID, username);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Game doesn't exist")) {
                throw new ResponseException("Error: bad request", 400);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }

        return playerColor;
    }

    public void updateWinner(int gameID, ChessGame.Winner winner) {
        GameData gameData;
        try {
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Game doesn't exist")) {
                throw new ResponseException("Error: bad request", 400);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
        ChessGame game = gameData.game();
        game.setWinner(winner);
        GameData updatedData =
                new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        try {
            gameDAO.updateGame(gameID, updatedData);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Game doesn't exist")) {
                throw new ResponseException("Error: bad request", 400);
            } else {
                throw new ResponseException("Error: " + e.getMessage(), 500);
            }
        }
    }
}
