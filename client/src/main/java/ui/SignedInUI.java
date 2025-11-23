package ui;

import chessclient.ChessClient;
import chessclient.ServerFacade;

import static ui.Formatting.blueText;

public class SignedInUI implements UIState {
    ChessClient client;
    ServerFacade server;

    public SignedInUI(ChessClient client, ServerFacade server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public String handle(String input) {
        return "signed in";
    }

    public String printPrompt() {
        return blueText + "[SIGNED_IN] >>> ";
    }
}
