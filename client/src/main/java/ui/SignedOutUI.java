package ui;

import chessclient.ChessClient;
import chessclient.ClientException;
import chessclient.ServerFacade;
import request.LoginRequest;
import request.RegisterRequest;
import websocket.WebsocketFacade;

import java.util.Arrays;

import static ui.Formatting.*;

public class SignedOutUI implements UIState {
    ChessClient client;
    ServerFacade server;
    WebsocketFacade ws;

    public SignedOutUI(ChessClient client, ServerFacade server, WebsocketFacade ws) {
        this.client = client;
        this.server = server;
        this.ws = ws;
    }

    @Override
    public String handle(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String selection = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (selection) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String printPrompt() {
        return "\n" + blueText + "[SIGNED_OUT] >>> ";
    }

    String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to sign into existing account
                quit - quit chess program
                help - display possible commands (current menu)""";
    }

    String register(String... params) {
        if (params.length != 3) {
            return errorText + "Expected: \"register <USERNAME> <PASSWORD> <EMAIL>\"" + reset;
        }
        try {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            server.register(request);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return errorText + "Expected: \"register <USERNAME> <PASSWORD> <EMAIL>\"" + reset;
            } else if (e.getCode() == 403) {
                return errorText + "Username is already taken" + reset;
            } else {
                return errorText + "Unexpected error, please try again. " +
                        "If this fails again, please restart program" + reset;
            }
        }
        return login(params[0], params[1]);
    }

    public String login(String... params) {
        if (params.length != 2) {
            return errorText + "Expected: \"login <USERNAME> <PASSWORD>\"" + reset;
        }
        try {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            client.setAuthToken(server.login(loginRequest).authToken());
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return errorText + "Expected: \"login <USERNAME> <PASSWORD>\"" + reset;
            } else if (e.getCode() == 401) {
                return errorText + "Invalid username or password" + reset;
            } else {
                return errorText + "Unexpected error, please try again. " +
                        "If this fails again, please restart program" + reset;
            }
        }
        client.setUser(params[0]);
        client.setState(new SignedInUI(client, server, ws));
        return "Signed in as " + params[0];
    }
}
