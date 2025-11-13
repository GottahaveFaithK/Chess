package chessclient;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) throws ClientException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {

    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to sign into existing account
                    quit - quit chess program
                    help - display possible commands (current menu)
                    """;
        } else {
            return """
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - signs out of current account
                    quit - quit chess program
                    help - display possible commands (current menu)
                    """;
        }
    }

    public void quit() {
        //quit game
    }

    public void register() {
        //register
    }

    public void login() {
        //login
    }

    public void logout() {
        //logout
    }

    public void createGame() {
        //let's play
    }

    public void listGames() {
        //fresh off the press
        //if it gets a client exception debug and name based off error message
    }

    public void playGame() {
        //join game
    }

    public void observeGame() {
        //watch game
    }

    private void assertSignedIn() {
        //check if user is signed in
    }

}
