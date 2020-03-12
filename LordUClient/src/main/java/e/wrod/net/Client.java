package e.wrod.net;

import e.wrod.net.utils.MyWebSocketClient;
import e.wrod.net.view.LoginPage;
import org.apache.log4j.Logger;

import java.net.URI;

public class Client {
    private static Logger logger = Logger.getLogger(Client.class);

    public static void main(String[] args) {
        try {
            MyWebSocketClient ws = new MyWebSocketClient(new URI("ws://localhost:18090/websocket"));
            ws.connect();
            new LoginPage(ws);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("URL format Exception:" + e);
        }
    }
}
