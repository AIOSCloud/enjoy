package e.word.net.util;

import com.alibaba.fastjson.JSON;
import e.word.net.common.*;
import e.word.net.model.Card;
import e.word.net.model.Event;
import e.word.net.model.User;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.List;

public class MyWebSocketClient extends WebSocketClient {
    Logger logger = Logger.getLogger(MyWebSocketClient.class);

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.debug("------onOpen---------");
    }

    @Override
    public void onMessage(String message) {
        // TODO: 2020/3/17 接受服务端发送的消息
        Event event = JSON.parseObject(message, Event.class);
        if (event.getType().equals("建立链接")) {
            logger.debug("建立连接。。。。。。");
        } else if (event.getType().equals("发牌")) {
            logger.debug(message);
            // TODO: 2020/3/17 根据牌来抢地主
        } else if (event.getType().equals("出牌")) {
            logger.debug("出牌.....");
            // TODO: 2020/3/17 调用机器人进行数据核算
            AIPlayer player = new AIPlayer(event.getPlayers(), event.getShows(), event.isNext());
            // TODO: 2020/3/17 机器人计算结果返回数据
            List<Card> cards = player.play();
            Event e = new Event();
            e.getShows().addAll(cards);
            e.setType("出牌");
            send(JSON.toJSONString(cards));
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.debug(s);
        logger.debug("------onClose---------");
    }

    @Override
    public void onError(Exception e) {
        logger.debug(e);
        logger.debug("服务链接失败");
    }
}
