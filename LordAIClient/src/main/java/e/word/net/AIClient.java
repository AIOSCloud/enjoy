package e.word.net;

import com.alibaba.fastjson.JSON;
import e.word.net.model.Event;
import e.word.net.model.User;
import e.word.net.util.MyHttpClient;
import e.word.net.util.MyWebSocketClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;

import java.net.URI;

public class AIClient {
    Logger logger = Logger.getLogger(AIClient.class);
    private String uri = "ws://localhost:8090/websocket";
    MyWebSocketClient ws;

    public void login() {

    }

    public void createLink() {
        while (!ws.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            second(1);
            logger.debug("链接初始化中，请稍后.....");
        }
        User user = new User();
        user.setUserName("AI");
        user.setRobot(true);
        Event event = new Event();
        event.setType("建立链接");
        event.setUser(user);
        ws.send(JSON.toJSONString(event));
    }

    public void createRoom() {
        while (!ws.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            second(1);
            logger.debug("链接初始化中，请稍后.....");
        }
        Event event = new Event();
        event.setType("创建房间");
        ws.send(JSON.toJSONString(event));
    }

    public void Init() {
        try {
            ws = new MyWebSocketClient(new URI(uri));
            ws.connect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("AI客户端接入失败");
        }
    }

    public void second(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (Exception e) {
            logger.debug("线程休眠失败......");
        }
    }

    public static void main(String[] args) {
        AIClient client = new AIClient();
        // TODO: 2020/3/17 客户端初始化 
        client.Init();
        // TODO: 2020/3/17 AI客户端模拟登录
        client.createLink();
    }
}
