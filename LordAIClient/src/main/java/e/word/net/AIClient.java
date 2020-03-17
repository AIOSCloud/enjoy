package e.word.net;

import com.alibaba.fastjson.JSONObject;
import e.word.net.util.MyWebSocketClient;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;

import java.net.URI;

public class AIClient {
    Logger logger = Logger.getLogger(AIClient.class);
    private String uri = "ws://localhost:8090";
    MyWebSocketClient ws;

    AIClient() {
        try {
            ws = new MyWebSocketClient(new URI(uri));
            ws.connect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("AI客户端接入失败");
        }
    }

    public static void main(String[] args) {
        AIClient client = new AIClient();
    }
}
