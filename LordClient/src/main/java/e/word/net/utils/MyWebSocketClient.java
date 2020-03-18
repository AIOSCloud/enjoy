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
            logger.debug("发牌......" + event.getUser());
            page.user = event.getUser();
            page.users = event.getUsers();
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
            page.time[1].setVisible(true);
            int i = 10;
            while (i >= 0 && page.isRun) {
                page.second(1);
                page.time[1].setText("倒计时:" + i--);
            }
            if (i == -1) {
                page.time[1].setText("不抢");
                page.landlord[0].setVisible(false);
                page.landlord[1].setVisible(false);
                // TODO: 2020/3/17 不抢地主
                Event result = new Event();
                result.setType("抢地主");
                result.setLord(false);
                result.setUser(page.user);
                this.send(JSON.toJSONString(result));
            }

        } else if (event.getType().equals("地主")) {
            page.isRun = true;
            //获取地主牌
            List<Card> lordCards = event.getLordList();
            //设置地主
            page.lordFlag = event.getLordIndex();
            page.turn = event.getTurn();
            page.time[page.lordFlag].setText("抢地主");
            page.setLord(page.lordFlag);
            for (int i = 0; i < lordCards.size(); i++) {
                page.lordList.get(i).setCard(lordCards.get(i));
                page.lordList.get(i).turnFront();
            }
            page.second(5);
            for (JCard card : page.lordList) {
                card.setCanClick(true);
                if (page.lordFlag != page.user.getIndex()) {
                    card.turnRear();
                }
                page.players[page.lordFlag].add(card);
            }
            Common.order(page.players[page.lordFlag]);
            Common.rePosition(page, page.players[page.lordFlag], page.lordFlag);
            if (page.turn == page.user.getIndex()) {
                page.publishCard[0].setVisible(true);
                page.publishCard[1].setVisible(true);
            }
            page.time[page.turn].setVisible(true);
            int i = 30;
            while (i >= 0 && page.isRun) {
                page.second(1);
                page.time[page.turn].setText("倒计时:" + i--);
            }
            if (i == -1) {
                page.time[page.turn].setText("不要");
                if (page.turn == page.user.getIndex()) {
                    page.publishCard[0].setVisible(false);
                    page.publishCard[1].setVisible(false);
                }
                // TODO: 2020/3/18 发送出牌消息
                Event result = new Event();
                result.setType("出牌");
            }
        } else if (event.getType().equals("出牌")) {
            page.turn = event.getTurn();
            // TODO: 2020/3/18 获取用户出的牌
            page.publishCard[0].setVisible(false);
            page.publishCard[1].setVisible(false);
            List<Card> showCards = event.getShows();
            page.shows[1].clear();
            int i = 30;
            while (i >= 0 && page.isRun) {
                page.second(1);
                page.time[1].setText("倒计时:" + i--);
            }
            if (i == -1) {
                page.time[page.turn].setText("不要");
                if (page.turn == page.user.getIndex()) {
                    page.publishCard[0].setVisible(false);
                    page.publishCard[1].setVisible(false);
                }
                // TODO: 2020/3/18 发送出牌消息
                Event result = new Event();
                result.setType("出牌");
            }
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
