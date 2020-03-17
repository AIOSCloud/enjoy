package e.wrod.net.utils;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MyWebSocketClient extends WebSocketClient {
    Logger logger = Logger.getLogger(MyWebSocketClient.class);

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.debug("open......");
    }

    @Override
    public void onMessage(String s) {
        logger.debug("on message......");
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.debug("close......");
    }

    @Override
    public void onError(Exception e) {
        logger.debug("onError......");
    }
}
