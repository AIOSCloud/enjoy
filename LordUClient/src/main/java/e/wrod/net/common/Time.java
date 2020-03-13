package e.wrod.net.common;

import e.wrod.net.component.JCard;
import e.wrod.net.view.OffLinePage;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Time extends Thread {
    private Logger logger = Logger.getLogger(Time.class);
    OffLinePage main;
    boolean isRun = true;
    int i = 10;

    public void setIsRun(boolean isRun) {
        this.isRun = isRun;
    }

    public Time(OffLinePage main, int i) {
        this.main = main;
        this.i = i;
    }

    @Override
    public void run() {
        while (i > -1 && isRun) {
            main.getTime()[1].setText("倒计时:" + i--);
            second(1);// 等一秒
        }
        if (i == -1)// 正常终结，说明超时
            main.getTime()[1].setText("不抢");
        main.getLandlord()[0].setVisible(false);
        main.getLandlord()[1].setVisible(false);
        //设置牌为可点击
        for (JCard card2 : main.getPlayerList()[1]) {
            card2.setCanClick(true);// 可被点击
        }
        // 如果自己抢到地主
        if (main.getTime()[1].getText().equals("抢地主")) {
            logger.debug("用户抢地主");
            // 得到地主牌
            main.getPlayerList()[1].addAll(main.getLordList());
            openlord(true);
            second(2);// 等待五秒
            Common.order(main.getPlayerList()[1]);
            Common.rePosition(main, main.getPlayerList()[1], 1);
            setLord(1);
        } else {
            // 电脑选地主
            logger.debug("电脑抢地主");
            if (Common.getScore(main.getPlayerList()[0]) < Common.getScore(main.getPlayerList()[2])) {
                main.getTime()[2].setText("抢地主");
                main.getTime()[2].setVisible(true);
                setLord(2);// 设定地主
                openlord(true);
                second(3);
                main.getPlayerList()[2].addAll(main.getLordList());
                Common.order(main.getPlayerList()[2]);
                Common.rePosition(main, main.getPlayerList()[2], 2);
                openlord(false);
            } else {
                main.getTime()[0].setText("抢地主");
                main.getTime()[0].setVisible(true);
                setLord(0);// 设定地主
                openlord(true);
                second(3);
                main.getPlayerList()[0].addAll(main.getLordList());
                Common.order(main.getPlayerList()[0]);
                Common.rePosition(main, main.getPlayerList()[0], 0);
            }
        }
        // 选完地主后 关闭地主按钮
        main.getLandlord()[0].setVisible(false);
        main.getLandlord()[1].setVisible(false);
        turnOn(false);
        for (int i = 0; i < 3; i++) {
            main.getTime()[i].setText("不要");
            main.getTime()[i].setVisible(false);
        }
        // 开始游戏 根据地主不同顺序不同
        main.setTurn(main.getLordFlag());
        while (true) {
            //我
            logger.debug("用户:" + main.getTurn() + " 出牌");
            if (main.getTurn() == 1) {
                turnOn(true);// 出牌按钮 --我出牌
                timeWait(30, 1);// 我自己的定时器
                turnOn(false);//选完关闭
                main.setTurn((main.getTurn() + 1) % 3);
                if (win())//判断输赢
                    break;
            }
            if (main.getTurn() == 0) {
                computer0();
                main.setTurn((main.getTurn() + 1) % 3);
                if (win())//判断输赢
                    break;
            }
            if (main.getTurn() == 2) {
                computer2();
                main.setTurn((main.getTurn() + 1) % 3);
                if (win())//判断输赢
                    break;
            }
        }
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

    // 地主牌翻看
    public void openlord(boolean is) {
        for (int i = 0; i < 3; i++) {
            if (is)
                main.getLordList().get(i).turnFront(); // 地主牌翻看
            else {
                main.getLordList().get(i).turnRear(); // 地主牌闭合
            }
            main.getLordList().get(i).setCanClick(true);
        }
    }

    // 设定地主
    public void setLord(int i) {
        Point point = new Point();
        if (i == 1)// 我是地主
        {
            point.x = 80;
            point.y = 430;
            main.setLordFlag(1);// 设定地主
        }
        if (i == 0) {
            point.x = 80;
            point.y = 20;
            main.setLordFlag(0);
        }
        if (i == 2) {
            point.x = 700;
            point.y = 20;
            main.setLordFlag(2);
        }
        main.getLord().setLocation(point);
        main.getLord().setVisible(true);
    }

    // 打开出牌按钮
    public void turnOn(boolean flag) {
        main.getPublishCard()[0].setVisible(flag);
        main.getPublishCard()[1].setVisible(flag);
    }

    // 电脑0走牌(我代表1)
    public void computer0() {
        timeWait(1, 0); // 定时
        ShowCard(0); // 出牌

    }

    // 电脑2走牌(我代表1)
    public void computer2() {
        timeWait(1, 2); // 定时
        ShowCard(2); // 出牌
    }

    // 走牌
    public void ShowCard(int role) {
        Model model = Compute.getModel(main.getPlayerList()[role]);
        // 待走的牌
        List<JCard> cards = new ArrayList<>();
        // 如果是主动出牌
        if (main.getTime()[(role + 1) % 3].getText().equals("不要")
                && main.getTime()[(role + 2) % 3].getText().equals("不要")) {
            // 有单出单 (除开3带，飞机能带的单牌)
            if (model.getA1().size() > (model.getA111222().size() * 2 + model.getA3().size())) {
                cards.addAll(model.getA1().get(model.getA1().size() - 1));
            }// 有对子出对子 (除开3带，飞机)
            else if (model.getA2().size() > (model.getA111222().size() * 2 + model.getA3().size())) {
                cards.addAll(model.getA2().get(model.getA2().size() - 1));
            }// 有顺子出顺子
            else if (model.getA123().size() > 0) {
                cards.addAll(model.getA123().get(model.getA123().size() - 1));
            }// 有3带就出3带，没有就出光3
            else if (model.getA3().size() > 0) {
                // 3带单,且非关键时刻不能带王，2
                if (model.getA1().size() > 0) {
                    cards.addAll(model.getA1().get(model.getA1().size() - 1));
                }// 3带对
                else if (model.getA2().size() > 0) {
                    cards.addAll(model.getA2().get(model.getA2().size() - 1));
                }
                cards.addAll(model.getA3().get(model.getA3().size() - 1));
            }// 有双顺出双顺
            else if (model.getA112233().size() > 0) {
                cards.addAll(model.getA112233().get(model.getA112233().size() - 1));
            }// 有飞机出飞机
            else if (model.getA111222().size() > 0) {
                // 带单
                if (model.getA1().size() > 2) {
                    cards.addAll(model.getA111222().get(model.getA111222().size() - 1));
                    for (int i = 0; i < 2; i++)
                        cards.addAll(model.getA1().get(i));
                } else if (model.getA2().size() > 2)// 带双
                {
                    cards.addAll(model.getA111222().get(model.getA111222().size() - 1));
                    for (int i = 0; i < 2; i++)
                        cards.addAll(model.getA2().get(i));
                } else {
                    cards.addAll(model.getA111222().get(model.getA111222().size() - 1));
                }
                // 有炸弹出炸弹
            } else if (model.getA4().size() > 0) {
                // 4带2,1
                int sizea1 = model.getA1().size();
                int sizea2 = model.getA2().size();
                if (sizea1 >= 2) {
                    cards.addAll(model.getA1().get(sizea1 - 1));
                    cards.addAll(model.getA1().get(sizea1 - 2));
                    cards.addAll(model.getA4().get(0));
                } else if (sizea2 >= 2) {
                    cards.addAll(model.getA2().get(sizea2 - 1));
                    cards.addAll(model.getA2().get(sizea2 - 2));
                    cards.addAll(model.getA4().get(0));
                } else {// 直接炸
                    cards.addAll(model.getA4().get(0));

                }
            }
        }// 如果是跟牌
        else {
            List<JCard> player = main.getCurrentList()[(role + 2) % 3].size() > 0
                    ? main.getCurrentList()[(role + 2) % 3]
                    : main.getCurrentList()[(role + 1) % 3];

            CardType cType = Common.jugdeType(player);
            logger.debug("判断出牌的类型为:" + cType);
            //如果是单牌
            if (cType == CardType.c1) {
                AI_1(model.a1, player, cards, role);
            }//如果是对子
            else if (cType == CardType.c2) {
                AI_1(model.a2, player, cards, role);
            }//3带
            else if (cType == CardType.c3) {
                AI_1(model.a3, player, cards, role);
            }//炸弹
            else if (cType == CardType.c4) {
                AI_1(model.a4, player, cards, role);
            }//如果是3带1
            else if (cType == CardType.c31) {
                //偏家 涉及到拆牌
                //if((role+1)%3==main.dizhuFlag)
                AI_2(model.a3, model.a1, player, cards, role);
            }//如果是3带2
            else if (cType == CardType.c32) {
                //偏家
                //if((role+1)%3==main.dizhuFlag)
                AI_2(model.a3, model.a2, player, cards, role);
            }//如果是4带11
            else if (cType == CardType.c411) {
                AI_5(model.a4, model.a1, player, cards, role);
            }
            //如果是4带22
            else if (cType == CardType.c422) {
                AI_5(model.a4, model.a2, player, cards, role);
            }
            //顺子
            else if (cType == CardType.c123) {
                AI_3(model.a123, player, cards, role);
            }
            //双顺
            else if (cType == CardType.c1122) {
                AI_3(model.a112233, player, cards, role);
            }
            //飞机带单
            else if (cType == CardType.c11122234) {
                AI_4(model.a111222, model.a1, player, cards, role);
            }
            //飞机带对
            else if (cType == CardType.c1112223344) {
                AI_4(model.a111222, model.a2, player, cards, role);
            }
            //炸弹
            if (cards.size() == 0) {
                int len4 = model.a4.size();
                if (len4 > 0)
                    cards.addAll(model.a4.get(len4 - 1));
            }
        }
        // 定位出牌
        main.getCurrentList()[role].clear();
        if (cards.size() > 0) {
            Point point = new Point();
            if (role == 0)
                point.x = 200;
            if (role == 2)
                point.x = 550;
            point.y = (400 / 2) - (cards.size() + 1) * 15 / 2;// 屏幕中部
            // 将name转换成Card
            for (int i = 0, len = cards.size(); i < len; i++) {
                for (JCard card : cards) {
                    Common.move(card, card.getLocation(), point);
                    point.y += 15;
                    main.getCurrentList()[role].add(card);
                    main.getPlayerList()[role].remove(card);
                }
            }
            Common.rePosition(main, main.getPlayerList()[role], role);
        } else {
            main.getTime()[role].setVisible(true);
            main.getTime()[role].setText("不要");
        }
        for (JCard card : main.getCurrentList()[role]) {
            logger.debug("用户:" + role + "出牌记录数为:" + main.getCurrentList()[role].size());
            card.turnFront();
        }
    }

    //顺子
    public void AI_3(List<List<JCard>> model, List<JCard> player, List<JCard> list, int role) {

        for (int i = 0, len = model.size(); i < len; i++) {
            List<JCard> flow = model.get(i);
            if (flow.size() == player.size() && Common.getValue(model.get(i).get(0)) > Common.getValue(player.get(0))) {
                list.addAll(model.get(i));
                return;
            }
        }
    }

    //飞机带单，双
    public void AI_4(List<List<JCard>> model1, List<List<JCard>> model2, List<JCard> player, List<JCard> list, int role) {
        //排序按重复数
        Common.order(player);
        int len1 = model1.size();
        int len2 = model2.size();

        if (len1 < 1 || len2 < 1)
            return;
        for (int i = 0; i < len1; i++) {
            List<JCard> plan = model1.get(i);
            List<JCard> twos = model2.get(0);
            if ((plan.size() / 3 <= len2) && (plan.size() * (3 + twos.size()) == player.size()) && Common.getValue(model1.get(i).get(0)) > Common.getValue(player.get(0))) {
                list.addAll(model1.get(i));
                for (int j = 1; j <= plan.size(); j++)
                    list.addAll(model2.get(len2 - j));
            }
        }
    }

    //4带1，2
    public void AI_5(List<List<JCard>> model1, List<List<JCard>> model2, List<JCard> player, List<JCard> list, int role) {
        //排序按重复数
        Common.order(player);
        int len1 = model1.size();
        int len2 = model2.size();

        if (len1 < 1 || len2 < 2)
            return;
        for (int i = 0; i < len1; i++) {
            if (Common.getValue(model1.get(i).get(0)) > Common.getValue(player.get(0))) {
                list.addAll(model1.get(i));
                for (int j = 1; j <= 2; j++)
                    list.addAll(model2.get(len2 - j));
            }
        }
    }

    //单牌，对子，3个，4个,通用
    public void AI_1(List<List<JCard>> model, List<JCard> player, List<JCard> list, int role) {
        //顶家
        if ((role + 1) % 3 == main.getLordFlag()) {
            for (int i = 0, len = model.size(); i < len; i++) {
                if (Common.getValue(model.get(i).get(0)) > Common.getValue(list.get(0))) {
                    list.add(model.get(i).get(0));
                    break;
                }
            }
        } else {//偏家
            for (int len = model.size(), i = len - 1; i >= 0; i--) {
                if (Common.getValue(model.get(i).get(0)) > Common.getValue(player.get(0))) {
                    list.add(model.get(i).get(0));
                    break;
                }
            }
        }
    }

    //3带1,2,4带1,2
    public void AI_2(List<List<JCard>> model1, List<List<JCard>> model2, List<JCard> player, List<JCard> list, int role) {
        //model1是主牌,model2是带牌,player是玩家出的牌,,list是准备回的牌
        //排序按重复数
        Common.order(player);
        int len1 = model1.size();
        int len2 = model2.size();
        //如果有王直接炸了
        if (len1 > 0 && model1.get(0).size() < 10) {
            list.addAll(model1.get(0));
            System.out.println("王炸");
            return;
        }
        if (len1 < 1 || len2 < 1)
            return;
        for (int len = len1, i = len - 1; i >= 0; i--) {
            if (Common.getValue(model1.get(i).get(0)) > Common.getValue(player.get(0))) {
                list.addAll(model1.get(i));
                break;
            }
        }
        list.addAll(model2.get(len2 - 1));
        if (list.size() < 2)
            list.clear();
    }

    // 延时，模拟时钟
    public void timeWait(int n, int player) {
        logger.debug("初始化计时器......");
        if (main.getCurrentList()[player].size() > 0)
            Common.hideCards(main.getCurrentList()[player]);
        // 如果是我，10秒到后直接下一家出牌
        logger.debug("是否下一个用户" + main.isNextPlayer() + " 倒计时变更" + n);
        if (player == 1) {
            int m = n;
            while (!main.isNextPlayer() && m >= 0) {
                // main.container.setComponentZOrder(main.time[player], 0);
                logger.debug("倒计时" + m);
                main.getTime()[player].setText("倒计时:" + m);
                main.getTime()[player].setVisible(true);
                second(1);
                m--;
            }
            if (i == -1) {
                main.getTime()[player].setText("超时");
            }
            main.setNextPlayer(false);
        } else {
            for (int i = n; i >= 0; i--) {
                second(1);
                // main.container.setComponentZOrder(main.time[player], 0);
                main.getTime()[player].setText("倒计时:" + i);
                main.getTime()[player].setVisible(true);
            }
        }
        main.getTime()[player].setVisible(false);
    }

    //通过name估值
    public int getValueInt(String n) {
        String name[] = n.split(",");
        String s = name[0];
        int i = Integer.parseInt(s.substring(2, s.length()));
        if (s.substring(0, 1).equals("5"))
            i += 3;
        if (s.substring(2, s.length()).equals("1") || s.substring(2, s.length()).equals("2"))
            i += 13;
        return i;
    }

    //判断输赢
    public boolean win() {
        for (int i = 0; i < 3; i++) {
            if (main.getPlayerList()[i].size() == 0) {
                String s;
                if (i == 1) {
                    s = "恭喜你，胜利了!";
                } else {
                    s = "恭喜电脑" + i + ",赢了! 你的智商有待提高哦";
                }
                JOptionPane.showMessageDialog(main, s);
                return true;
            }
        }
        return false;
    }
}
