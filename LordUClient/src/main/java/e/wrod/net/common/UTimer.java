package e.wrod.net.common;

import e.wrod.net.component.JCard;
import e.wrod.net.model.User;
import e.wrod.net.view.AIPage;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.List;

public class UTimer extends Thread {
    Logger logger = Logger.getLogger(UTimer.class);
    AIPage page;
    int position;
    int i = 10;
    boolean landLord = true;

    public void setLoadLord(boolean landLord) {
        this.landLord = landLord;
    }

    public UTimer(AIPage page, int position) {
        this.page = page;
        this.position = position;
    }

    @Override
    public void run() {
        int befor = Common2.befor(position);
        int mine = Common2.mine(position);
        int next = Common2.next(position);
        // TODO: 2020/3/15 主线程 监听信息
        // TODO: 2020/3/15 抢地主
        while (i > -1 && landLord) {
            page.getTime()[1].setText("倒计时:" + i--);
            second(1);// 等一秒
        }
        if (i == -1)// 正常终结，说明超时
            page.getTime()[1].setText("不抢");
        page.getLandlord()[0].setVisible(false);
        page.getLandlord()[1].setVisible(false);
        //设置牌为可点击
        for (JCard card : page.getUsers().get(position).getPlayers()) {
            card.setCanClick(true);
        }
        //用户抢地主
        if (page.getUsers().get(mine).isLord()) {
            logger.debug("用户抢地主");
            // 得到地主牌
            page.getUsers().get(mine).getPlayers().addAll(page.getLordList());
            page.getUsers().get(mine).setTurn(true);
            openlord(true);
            second(2);// 等待五秒
            openlord(false);
            Common2.order(page.getUsers().get(mine).getPlayers());
            Common2.rePosition(page, page.getUsers().get(mine).getPlayers(), position, position);
            setLord(mine, position);
            updateLord(page.getUsers(), mine);
        } else {
            // 电脑选地主
            logger.debug("电脑抢地主");
            if (Common.getScore(page.getUsers().get(Common2.befor(position)).getPlayers())
                    < Common.getScore(page.getUsers().get(Common2.next(position)).getPlayers())) {
                page.getTime()[2].setText("抢地主");
                page.getTime()[2].setVisible(true);
                setLord(next, position);// 设定地主
                openlord(true);
                second(5);
                openlord(false);
                page.getUsers().get(next).getPlayers().addAll(page.getLordList());
                page.getUsers().get(next).setTurn(true);
                Common.order(page.getUsers().get(next).getPlayers());
                Common2.rePosition(page, page.getUsers().get(next).getPlayers(), next, position);
                updateLord(page.getUsers(), next);
            } else {
                page.getTime()[0].setText("抢地主");
                page.getTime()[0].setVisible(true);
                setLord(befor, position);// 设定地主
                openlord(true);
                second(3);
                openlord(false);
                page.getUsers().get(befor).getPlayers().addAll(page.getLordList());
                page.getUsers().get(befor).setTurn(true);
                Common.order(page.getUsers().get(befor).getPlayers());
                Common2.rePosition(page, page.getUsers().get(befor).getPlayers(), befor, position);
                updateLord(page.getUsers(), befor);
            }
        }
        for (int i = 0; i < 3; i++) {
            page.getTime()[i].setText("不要");
            page.getTime()[i].setVisible(false);
        }
        // TODO: 2020/3/15 出牌，直到分出胜负
        while (true) {
            logger.debug("出牌");
            if (page.getUsers().get(mine).isTurn()) {
                logger.debug("用户自己出牌。。。。。" + mine);
                // TODO: 2020/3/15 如果轮到自己出牌
                page.getPublishCard()[0].setVisible(true);
                page.getPublishCard()[1].setVisible(true);
                timeWait(30, 1, position);
            }
            if (page.getUsers().get(next).isTurn()) {
                logger.debug("下一家用户出牌。。。。。" + next);
                timeWait(10, 2, position);
                // TODO: 2020/3/15 如果是下家出牌
                AIClient client = new AIClient(page.getUsers().get(next), next);
                List<JCard> cards = client.showCard();
                showCards(cards, next, position);
            }
            if (page.getUsers().get(befor).isTurn()) {
                logger.debug("上一家用户出牌。。。。。" + befor);
                // TODO: 2020/3/15 如果是上家出牌
                timeWait(10, 0, position);
                // TODO: 2020/3/15 如果是下家出牌
                AIClient client = new AIClient(page.getUsers().get(befor), befor);
                List<JCard> cards = client.showCard();
                showCards(cards, befor, position);
            }
        }
    }

    public void updateLord(List<User> users, int role) {
        for (User user : users) {
            user.setLordRole(role);
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
        page.getUsers().get(i).setLordFlag(true);
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
        int befor = Common2.befor(position);
        int mine = Common2.mine(position);
        int next = Common2.next(position);
        // 如果是我，10秒到后直接下一家出牌
        if (player == 1) {
            int m = n;
            while (page.getUsers().get(mine).isTurn() && m >= 0) {
                logger.debug("倒计时" + m);
                page.getTime()[player].setText("倒计时:" + m);
                page.getTime()[player].setVisible(true);
                second(1);
                m--;
            }
            if (i == -1) {
                page.getTime()[1].setText("超时");
            }
            page.getUsers().get(mine).setTurn(false);
            page.getUsers().get(next).setTurn(true);
            page.getUsers().get(next).setFollow(page.getUsers().get(mine).isFollow());
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
        page.getUsers().get(mine).getShows().clear();
        // 定位出牌
        if (cards.size() > 0) {
            Point point = new Point();
            if (role == befor)
                point.x = 300;
            if (role == next)
                point.x = 500;
            point.y = (400 / 2) - (cards.size() + 1) * 15 / 2;// 屏幕中部
            // 将name转换成Card
            for (JCard card : cards) {
                Common.move(card, card.getLocation(), point);
                point.y += 15;
                page.getUsers().get((role + 1) % 3).getShows().add(card);
                page.getUsers().get((role + 1) % 3).setTurn(true);
                page.getUsers().get(role).setTurn(false);
                page.getUsers().get(role).getPlayers().remove(card);
            }
            Common2.rePosition(page, page.getUsers().get(role).getPlayers(), role, position);
        } else {
            // TODO: 2020/3/15 出牌 
            page.getUsers().get((role + 1) % 3).getShows().addAll(page.getUsers().get(role - 1).getShows());
            page.getUsers().get((role + 1) % 3).setTurn(true);
            page.getUsers().get(role).setTurn(false);
            page.getTime()[role].setVisible(true);
            page.getTime()[role].setText("不要");
        }
        for (JCard card : page.getUsers().get((role + 1) % 3).getShows()) {
            card.turnFront();
        }
    }
}
