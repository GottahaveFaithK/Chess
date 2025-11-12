package chessclient;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(String serverUrl) throws ClientException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {

    }
}
