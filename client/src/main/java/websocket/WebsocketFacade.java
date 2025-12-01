package websocket;

import chessclient.ClientException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {
    //pass the messages to the client via notification handler
    NotificationHandler notificationHandler;
    Session session;

    public WebsocketFacade(String url, NotificationHandler notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            System.out.println("Connecting to: " + socketURI);
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println("triggered onMessage");
                    ServerMessage serverMessage = gsonMessages().fromJson(message, ServerMessage.class);
                    System.out.println("About to call notify: " + notificationHandler);
                    notificationHandler.notify(serverMessage);
                    System.out.println("notify call completed");
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ClientException(ex.getMessage(), 500);
        }
    }

    public static Gson gsonMessages() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ServerMessage.class,
                (JsonDeserializer<ServerMessage>) (el, type, ctx) -> {
                    JsonObject obj = el.getAsJsonObject();
                    String typeField = obj.get("serverMessageType").getAsString();

                    return switch (ServerMessage.ServerMessageType.valueOf(typeField)) {
                        case LOAD_GAME -> ctx.deserialize(el, LoadGameMessage.class);
                        case NOTIFICATION -> ctx.deserialize(el, NotificationMessage.class);
                        case ERROR -> ctx.deserialize(el, ErrorMessage.class);
                    };
                });

        return gsonBuilder.create();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        //doesn't have to do anything, allegedly
    }

    public void joinGame(String authToken, int gameID) throws ClientException {
        UserGameCommand joinCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);

        try {
            session.getBasicRemote().sendText(new Gson().toJson(joinCommand));
        } catch (IOException e) {
            throw new ClientException("Websocket Failed: " + e.getMessage(), 500);
        }
    }
}
