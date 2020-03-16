package e.wrod.net.common;

import e.wrod.net.component.JCard;
import e.wrod.net.view.AIPage;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.List;

public class UTimer extends Thread {
    Logger logger = Logger.getLogger(UTimer.class);
    AIPage page;
    int mine;
    int befor;
    int next;
    int i = 10;
    boolean run = true;

    public void isRun(boolean landLord) {
        this.run = landLord;
    }

    public UTimer(AIPage page, int mine, int befor, int next) {
        this.page = page;
        this.mine = mine;
        this.befor = befor;
        this.next = next;
    }

    @Override
    public void run() {
        // TODO: 2020/3/15 主线程 监听信息
        // TODO: 2020/3/15 抢地主
        while (i > -1 && run) {
            page.getTime()[1].setText("倒计时:" + i--);
            second(1);// 等一秒
        }
        if (i == -1)// 正常终结，说明超时
            page.getTime()[1].setText("不抢");
        page.getLandlord()[0].setVisible(false);
        page.getLandlord()[1].setVisible(false);
        //设置牌为可点击
        for (JCard card : page.getPlayerList()[mine]) {
            card.setCanClick(true);
        }
        //用户抢地主
        if (page.getLoadLord().get(mine) != null && page.getLoadLord().get(mine)) {
            logger.debug("用户抢地主");
            openlord(true);
            second(2);// 等待五秒
            openlord(false);
            // 得到地主牌
            page.getPlayerList()[mine].addAll(page.getLordList());
            page.getLordList().clear();
            page.setTurn(mine);
            page.setFollow(mine);
            Common2.order(page.getPlayerList()[mine]);
            Common2.rePosition(page, page.getPlayerList()[mine], mine, mine);
            setLord(mine, mine);
        } else {
            // 电脑选地主
            logger.debug("电脑抢地主");
            if (Common.getScore(page.getPlayerList()[befor])
                    < Common.getScore(page.getPlayerList()[next])) {
                page.getTime()[2].setText("抢地主");
                page.getTime()[2].setVisible(true);
                setLord(next, mine);// 设定地主
                openlord(true);
                second(5);
                openlord(false);
                page.getPlayerList()[next].addAll(page.getLordList());
                page.getLordList().clear();
                page.setTurn(next);
                page.setFollow(next);
                Common.order(page.getPlayerList()[next]);
                Common2.rePosition(page, page.getPlayerList()[next], next, mine);
            } else {
                page.getTime()[0].setText("抢地主");
                page.getTime()[0].setVisible(true);
                setLord(befor, mine);// 设定地主
                openlord(true);
                second(3);
                openlord(false);
                page.getPlayerList()[befor].addAll(page.getLordList());
                page.getLordList().clear();
                page.setTurn(befor);
                page.setFollow(befor);
                Common.order(page.getPlayerList()[befor]);
                Common2.rePosition(page, page.getPlayerList()[befor], befor, mine);
            }
        }
        // TODO: 2020/3/16 关闭倒计时窗口
        for (int i = 0; i < 3; i++) {
            page.getTime()[i].setText("不要");
            page.getTime()[i].setVisible(false);
        }
        // TODO: 2020/3/15 出牌，直到分出胜负
        while (true) {
            logger.debug("出牌");
            if (page.getTurn() == mine) {
                logger.debug("用户自己出牌。。。。。" + mine);
                // TODO: 2020/3/15 如果轮到自己出牌
                page.getPublishCard()[0].setVisible(true);
                page.getPublishCard()[1].setVisible(true);
                timeWait(10, mine, mine);
                List<JCard> shows = null;
                if (page.getFollow() != mine) {
                    shows = page.getCurrentList()[page.getFollow()];
                }
                AIClient client = new AIClient(page.getPlayerList()[mine], shows, page.getLordFlag(), mine);
                List<JCard> cards = client.plays();
                showCards(cards, mine, mine);
                page.getPublishCard()[0].setVisible(false);
                page.getPublishCard()[1].setVisible(false);
            }
            if (page.getTurn() == next) {
                logger.debug("下一家用户出牌。。。。。" + next);
                timeWait(1, next, mine);
                // TODO: 2020/3/15 如果是下家出牌
                List<JCard> shows = null;
                if (page.getFollow() != next) {
                    shows = page.getCurrentList()[page.getFollow()];
                }
                AIClient client = new AIClient(page.getPlayerList()[next], shows, page.getLordFlag(), next);
                List<JCard> cards = client.plays();
                showCards(cards, next, mine);
            }
            if (page.getTurn() == befor) {
                logger.debug("上一家用户出牌。。。。。" + befor);
                // TODO: 2020/3/15 如果是上家出牌
                timeWait(1, befor, befor);
                // TODO: 2020/3/15 如果是下家出牌
                List<JCard> shows = null;
                if (page.getFollow() != befor) {
                    shows = page.getCurrentList()[page.getFollow()];
                }
                AIClient client = new AIClient(page.getPlayerList()[befor], shows, page.getLordFlag(), befor);
                List<JCard> cards = client.plays();
                showCards(cards, befor, mine);
            }
        }
    }

    // 地主牌翻看
    public void openlord(boolean is) {
        for (int i = 0; i < 3; i++) {
            if (is)
                page.getLordList().get(i).turnFront(); // 地主牌翻看
            else {
                page.getLordList().get(i).turnRear(); // 地主牌闭合
            }
            page.getLordList().get(i).setCanClick(true);
        }
    }

    // 设定地主
    public void setLord(int i, int position) {
        int befor = Common2.befor(position);
        int mine = Common2.mine(position);
        int next = Common2.next(position);
        Point point = new Point();
        if (i == mine)// 我是地主
        {
            point.x = 100;
            point.y = 480;
        }
        if (i == befor) {
            point.x = 80;
            point.y = 20;
        }
        if (i == next) {
            point.x = 700;
            point.y = 20;
        }
        page.setLordFlag(i);
        page.getLord().setLocation(point);
        page.getLord().setVisible(true);
    }

    // 等待i秒
    public void second(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 延时，模拟时钟
    public void timeWait(int n, int player, int position) {
        logger.debug("初始化计时器......");
        if (page.getCurrentList()[player].size() > 0)
            Common.hideCards(page.getCurrentList()[player]);
        // 如果是我，10秒到后直接下一家出牌
        if (player == mine) {
            int m = n;
            while (page.getTurn() == mine && m >= 0) {
                logger.debug("倒计时" + m);
                page.getTime()[player].setText("倒计时:" + m);
                page.getTime()[player].setVisible(true);
                second(1);
                m--;
            }
            if (i == -1) {
                page.getTime()[1].setText("超时");
            }
            page.getPublishCard()[0].setVisible(false);
            page.getPublishCard()[1].setVisible(false);
        } else {
            for (int i = n; i >= 0; i--) {
                second(1);
                page.getTime()[player].setText("倒计时:" + i);
                page.getTime()[player].setVisible(true);
            }
        }
        page.getTime()[player].setVisible(false);
    }

    public void showCards(List<JCard> cards, int role, int position) {
        int befor = Common2.befor(position);
        int mine = Common2.mine(position);
        int next = Common2.next(position);
        // TODO: 2020/3/16 出牌时首先需要清除自己的currentList 
        page.getCurrentList()[role].clear();
        // 定位出牌
        if (cards.size() > 0) {
            Point point = new Point();
            if (role != mine) {
                if (role == befor)
                    point.x = 240;
                if (role == next)
                    point.x = 600;
                point.y = (400 / 2) - (cards.size() + 1) * 15 / 2;// 屏幕中部
                // 将name转换成Card
                for (JCard card : cards) {
                    Common.move(card, card.getLocation(), point);
                    point.y += 15;
                }
                page.getCurrentList()[role].addAll(cards);
                page.setTurn((role + 1) % 3);
                page.setFollow(role);
                page.getPlayerList()[role].removeAll(cards);
                Common2.rePosition(page, page.getPlayerList()[role], role, position);
            } else {
                point.x = (770 / 2) - (cards.size() + 1) * 15 / 2;
                point.y = 300;
                for (int i = 0, len = cards.size(); i < len; i++) {
                    JCard card = cards.get(i);
                    Common.move(card, card.getLocation(), point);
                    point.x += 15;
                }
                page.getCurrentList()[role].addAll(cards);
                page.setTurn((role + 1) % 3);
                page.setFollow(role);
                page.getPlayerList()[role].removeAll(cards);
                //重新理牌
                Common2.rePosition(page, page.getPlayerList()[role], role, position);
            }
        } else {
            // TODO: 2020/3/15 出牌
            page.setTurn((role + 1) % 3);
            page.getTime()[role].setVisible(true);
            page.getTime()[role].setText("不要");
        }
        for (JCard card : page.getCurrentList()[role]) {
            logger.debug("用户:" + role + " 出牌:" + card.getCard().getColor() + "-" + card.getCard().getNumber());
            card.turnFront();
        }
    }
}
