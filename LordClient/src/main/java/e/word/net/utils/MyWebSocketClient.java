package e.word.net.utils;

import com.alibaba.fastjson.JSON;
import e.word.net.common.Common;
import e.word.net.component.JCard;
import e.word.net.model.Card;
import e.word.net.model.Event;
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
        logger.debug(message);
        Event event = JSON.parseObject(message, Event.class);
        if (event.getType().equals("建立链接")) {
            logger.debug("建立连接......");
            page.user = event.getUser();
        } else if (event.getType().equals("发牌")) {
            logger.debug("发牌......" + event.getUser());
            page.user = event.getUser();
            page.users = event.getUsers();
            page.mine = event.getIndex();
            logger.debug("当前用户位置:" + page.mine);
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
            page.time[page.mine].setVisible(true);
            //抢地主计时 线程
            page.t = new Time(page, this, true, true);
            page.t.start();
        } else if (event.getType().equals("地主")) {
            logger.debug("跟新地主信息......");
            page.lastShowIndex = -1;
            //获取地主牌
            List<Card> lordCards = event.getLordList();
            //设置地主
            page.lordFlag = event.getLordIndex();
            page.turn = event.getTurn();
            page.time[page.lordFlag].setVisible(true);
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
            if (page.turn == event.getTurn()) {
                page.publishCard[0].setVisible(true);
                page.publishCard[1].setVisible(true);
            }
            page.time[page.turn].setVisible(true);
            page.t = new Time(page, this, true, false);
            page.t.start();
        } else if (event.getType().equals("出牌")) {
            page.t.isRun = false;
            int turn = event.getTurn();
            int mineIndex = event.getIndex();
            int lordIndex = event.getLordIndex();
            int showIndex = event.getShowIndex();
            boolean play = event.isPlay();
            int playIndex = event.getPlayIndex();
            //更新界面信息
            page.turn = turn;
            page.mine = mineIndex;
            page.showIndex = showIndex;
            page.shows[showIndex].clear();
            List<Card> showsCards = event.getShows();
            page.shows[turn].clear();
            page.time[turn].setVisible(true);
            page.time[(turn + 2) % 3].setVisible(false);
            if (mineIndex == turn) {
                //轮到自己出牌，展示出牌按钮
                page.publishCard[0].setVisible(true);
                page.publishCard[1].setVisible(true);
            }
            if (play) {
                page.lastShowIndex = showIndex;
                page.shows[page.lastShowIndex].clear();
                logger.debug("不是自己出牌，需要更新界面展示");
                //移除牌 到展示牌的集合
                for (int i = 0; i < event.getShows().size(); i++) {
                    page.players[showIndex].get(i).setCard(event.getShows().get(i));
                    page.shows[showIndex].add(page.players[showIndex].get(i));
                }
                //模拟一出牌面
                page.players[showIndex].removeAll(page.shows[showIndex]);
                // 出牌展示到界面上
                Point point = new Point();
                if (showIndex == 0) {
                    point.x = 240;
                } else {
                    point.x = 600;
                }
                // 屏幕中部
                point.y = (400 / 2) - (page.shows[page.showIndex].size() + 1) * 15 / 2;
                for (JCard card : page.shows[page.showIndex]) {
                    card.turnFront();
                    Common.move(card, card.getLocation(), point);
                    point.y += 15;
                }
                Common.order(page.players[page.turn]);
                Common.rePosition(page, page.players[page.showIndex], page.showIndex);
            } else {
                page.time[playIndex].setVisible(true);
                page.time[playIndex].setText("不要");
            }
            // 轮到输出牌，界面模拟倒计时
            page.t = new Time(page, this, true, false);
            page.t.start();
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
