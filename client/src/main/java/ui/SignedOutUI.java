package ui;

import chessclient.ChessClient;
import chessclient.ClientException;
import chessclient.ServerFacade;
import chessclient.State;
import request.LoginRequest;
import request.RegisterRequest;

import java.util.Arrays;

import static ui.Formatting.blueText;

public class SignedOutUI implements UIState {
    ChessClient client;
    ServerFacade server;

    public SignedOutUI(ChessClient client, ServerFacade server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public String handle(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String selection = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (selection) {
            case "register" -> register(params);
            case "login" -> login(params);
            default -> help();
        };
    }

    public String printPrompt() {
        return blueText + "[SIGNED_OUT] >>> ";
    }

    String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to sign into existing account
                quit - quit chess program
                help - display possible commands (current menu)
                """;
    }

    String register(String... params) {
        try {
        } catch (Exception e) {
            return e.getMessage();
        }
        if (params.length != 3) {
            return "Expected: \"register <USERNAME> <PASSWORD> <EMAIL>\"";
        }
        try {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            server.register(request);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return "Expected: \"register <USERNAME> <PASSWORD> <EMAIL>\"";
            } else if (e.getCode() == 403) {
                return "Username is already taken";
            } else {
                return "Unexpected error, please try again. If this fails again, please restart program";
            }
        }
        return login(params[0], params[1]);
    }

    public String login(String... params) {
        if (params.length != 2) {
            return "Expected: \"login <USERNAME> <PASSWORD>\"";
        }
        try {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            client.setAuthToken(server.login(loginRequest).authToken());
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return "Expected: \"login <USERNAME> <PASSWORD>\"";
            } else if (e.getCode() == 401) {
                return "Invalid username or password";
            } else {
                return "Unexpected error, please try again. If this fails again, please restart program";
            }
        }
        client.setUser(params[0]);
        client.setState(new SignedInUI(client, server));
        return "Signed in as " + params[0];
    }
}
