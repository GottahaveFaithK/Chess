package dataaccess;

import chess.*;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {

    @Test
    void createGameID() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        int id = gameDAO.createGame("cool name");
        assertEquals(1, id);
        int newId = gameDAO.createGame("other name");
        assertEquals(2, newId);
    }

    @Test
    void createGameAndGet() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        int id = gameDAO.createGame("cool name");
        assertEquals(1, id);
        var res = gameDAO.getGame(id);
        assertEquals(1, res.gameID());
        assertNull(res.whiteUsername());
        assertNull(res.blackUsername());
        assertEquals("cool name", res.gameName());

        ChessGame game = res.game();
        assertEquals(ChessGame.TeamColor.WHITE, game.getTeamTurn());
        assertNotNull(game.getBoard());
        ChessBoard board = new ChessBoard();
        assertEquals(board, game.getBoard());
    }

    @Test
    void createNullGameName() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
        assertEquals("Game name is null", ex.getMessage());
    }

    @Test
    void updateGame() throws DataAccessException, InvalidMoveException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        int id = gameDAO.createGame("cool name");
        GameData myGameData = gameDAO.getGame(id);
        ChessGame myGame = myGameData.game();
        myGame.makeMove(new ChessMove(new ChessPosition(1, 2),
                new ChessPosition(3, 3), null));
        GameData updatedGameData = new GameData(id, myGameData.whiteUsername(), myGameData.blackUsername(),
                myGameData.gameName(), myGame);
        gameDAO.updateGame(id, updatedGameData);
        var res = gameDAO.getGame(id);
        assertEquals(1, res.gameID());
        assertNull(res.whiteUsername());
        assertNull(res.blackUsername());
        assertEquals("cool name", res.gameName());
        ChessGame game = res.game();
        assertNull(game.getBoard().getPiece(new ChessPosition(1, 2)));
        assertNotNull(game.getBoard().getPiece(new ChessPosition(3, 3)));
        assertEquals(ChessGame.TeamColor.WHITE, game.getTeamTurn());
        assertNotNull(game.getBoard());
        assertEquals(updatedGameData.game().getBoard(), game.getBoard());
    }

    @Test
    void updateGameWrongID() throws DataAccessException, InvalidMoveException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        int id = gameDAO.createGame("cool name");
        GameData myGameData = gameDAO.getGame(id);
        ChessGame myGame = myGameData.game();
        myGame.makeMove(new ChessMove(new ChessPosition(1, 2),
                new ChessPosition(3, 3), null));
        int invalidId = 17;
        GameData updatedGameData = new GameData(invalidId, myGameData.whiteUsername(), myGameData.blackUsername(),
                myGameData.gameName(), myGame);
        DataAccessException ex = assertThrows(DataAccessException.class, ()
                -> gameDAO.updateGame(invalidId, updatedGameData));
        assertEquals("Game doesn't exist", ex.getMessage());
    }

    @Test
    void updateColor() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        int id = gameDAO.createGame("cool name");
        GameData myGameData = gameDAO.getGame(id);
        ChessGame myGame = myGameData.game();
        gameDAO.updateColor(myGameData, myGame, "WHITE", "white user");
        var res = gameDAO.getGame(id);
        assertEquals(1, res.gameID());
        assertEquals("white user", res.whiteUsername());
        assertNull(res.blackUsername());
        assertEquals("cool name", res.gameName());
        ChessGame game = res.game();
        assertEquals(ChessGame.TeamColor.WHITE, game.getTeamTurn());
        assertNotNull(game.getBoard());
        ChessBoard board = new ChessBoard();
        assertEquals(board, game.getBoard());
    }

    @Test
    void updateColorTaken() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        int id = gameDAO.createGame("cool name");
        GameData myGameData = gameDAO.getGame(id);
        ChessGame myGame = myGameData.game();
        gameDAO.updateColor(myGameData, myGame, "WHITE", "white user");
        DataAccessException ex = assertThrows(DataAccessException.class, ()
                -> gameDAO.updateColor(myGameData, myGame, "WHITE", "Other User"));
        assertEquals("Color already taken", ex.getMessage());
    }

    @Test
    void getGame() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        int id = gameDAO.createGame("cool name");
        var res = gameDAO.getGame(id);
        assertEquals(1, res.gameID());
        assertNull(res.whiteUsername());
        assertNull(res.blackUsername());
        assertEquals("cool name", res.gameName());

        ChessGame game = res.game();
        assertEquals(ChessGame.TeamColor.WHITE, game.getTeamTurn());
        assertNotNull(game.getBoard());
        ChessBoard board = new ChessBoard();
        assertEquals(board, game.getBoard());
    }

    @Test
    void getGameInvalidID() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDAO();
        gameDAO.deleteAllGames();
        gameDAO.createGame("cool name");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.getGame(99));
        assertEquals("Game ID is invalid", ex.getMessage());
    }

    @Test
    void listGames() throws DataAccessException {
        GameDAO gameDAO = new MemoryGameDAO();
        gameDAO.deleteAllGames();
        int idOne = gameDAO.createGame("Game");
        int idTwo = gameDAO.createGame("Other Game");
        Collection<GameData> games = gameDAO.listGames();
        assertEquals(3, games.size());


    }

    @Test
    void listGamesNegative() throws DataAccessException {
        //TODO so.... the invalid error is handled in the server. Don't want to switch that.
    }

    @Test
    void deleteAllGames() throws DataAccessException {

    }
}
