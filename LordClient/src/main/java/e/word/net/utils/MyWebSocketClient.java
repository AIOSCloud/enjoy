package e.word.net.utils;

import com.alibaba.fastjson.JSON;
import com.sun.security.auth.NTDomainPrincipal;
import e.word.net.common.Common;
import e.word.net.component.JCard;
import e.word.net.model.Card;
import e.word.net.model.Event;
import e.word.net.view.LoginPage;
import e.word.net.view.RoomPage;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.awt.*;
import java.net.URI;
import java.util.List;

public class MyWebSocketClient extends WebSocketClient {
    Logger logger = Logger.getLogger(MyWebSocketClient.class);
    RoomPage page;

    public MyWebSocketClient(URI serverUri, RoomPage page) {
        super(serverUri);
        this.page = page;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.debug(".........open......");
    }

    @Override
    public void onMessage(String message) {
        logger.debug("...........on message......");
        logger.debug(message);
        Event event = JSON.parseObject(message, Event.class);
        if (event.getType().equals("建立链接")) {
            logger.debug("建立连接......");
            page.user = event.getUser();
        } else if (event.getType().equals("发牌")) {
            logger.debug("发牌......");
            // TODO: 2020/3/17 煮面初始化
            //用户牌
            List<Card> playerCards = event.getPlayers();
            for (int i = 0; i < page.jCards.length; i++) {
                if (i >= 51) {//地主牌
                    Common.move(page.jCards[i], page.jCards[i].getLocation(), new Point(320 + (i - 51) * 80, 10));
                    page.lordList.add(page.jCards[i]);
                    continue;
                }
                Point point;
                switch (i % 3) {
                    case 0:
                        // TODO: 2020/3/12 玩家1
                        point = new Point(50, 60 + i * 5);
                        Common.move(page.jCards[i], page.jCards[i].getLocation(), point);
                        page.players[0].add(page.jCards[i]);
                        break;
                    case 1:
                        // TODO: 2020/3/12 玩家2:
                        point = new Point(180 + i * 7, 450);
                        Common.move(page.jCards[i], page.jCards[i].getLocation(), point);
                        page.players[1].add(page.jCards[i]);
                        break;
                    case 2:
                        //todo 玩家3
                        point = new Point(700, 60 + i * 5);
                        Common.move(page.jCards[i], page.jCards[i].getLocation(), point);
                        page.players[2].add(page.jCards[i]);
                        break;
                }
                page.container.setComponentZOrder(page.jCards[i], 0);
            }
            for (int i = 0; i < page.players[1].size(); i++) {
                page.players[1].get(i).setCard(playerCards.get(i));
                page.players[1].get(i).turnFront();
                page.players[1].get(i).setCanClick(true);
            }
            page.landlord[0].setVisible(true);
            page.landlord[1].setVisible(true);
        } else if (event.getType().equals("地主")) {
            //获取地主牌
            List<Card> lordCards = event.getLordList();
            //设置地主
            page.lordFlag = event.getLordIndex();
            page.turn = event.getTurn();
            for (int i = 0; i < lordCards.size(); i++) {
                page.lordList.get(i).setCard(lordCards.get(i));
                page.lordList.get(i).turnFront();
            }
            page.second(5);
            for (JCard card : page.lordList) {
                page.players[page.lordFlag].add(card);
            }
            Common.order(page.players[page.lordFlag]);

        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.debug("............close......");
    }

    @Override
    public void onError(Exception e) {
        logger.debug(".................onError......" + e);
    }
}
