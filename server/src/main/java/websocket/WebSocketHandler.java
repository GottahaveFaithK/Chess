package websocket;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {

    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {

    }

    //connect
    //make move
    //leave
    //resign
    //look at video at 11:02

    //for deserializing make move commands may need to do it twice
    //deserialize it to see what type it is
    //if it is a make move command, deserialize again but as a make move command
}
