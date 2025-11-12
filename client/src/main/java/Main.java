import chess.*;
import chessclient.ChessClient;
import chessclient.ClientException;
import model.GameData;
import ui.ClientChessboard;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new ChessClient(serverUrl).run();

        } catch (ClientException ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}