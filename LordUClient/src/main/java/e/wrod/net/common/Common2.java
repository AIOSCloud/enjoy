package e.wrod.net.common;

import e.wrod.net.component.JCard;
import e.wrod.net.view.RoomPage;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class Common2 {
    //排序
    public static void order(List<JCard> list) {
        list.sort(new Comparator<JCard>() {
            @Override
            public int compare(JCard c1, JCard c2) {
                return weight(c2) - weight(c1);
            }
        });
    }

    public static int weight(JCard card) {
        int color = card.getCard().getColor();
        int number = card.getCard().getNumber();
        //��������Ļ�
        if (color == 5 && number == 2) {
            return 100;
        }
        if (color == 5 && number == 1) {
            return 90;
        }
        if (color < 5 && number == 2) {
            return 80;
        }
        if (color < 5 && number == 1) {
            return 70;
        }
        return number;
    }

    public static int mine(int position) {
        return position;
    }

    public static int befor(int position) {
        if (position == 0) {
            return 2;
        } else if (position == 2) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int next(int position) {
        if (position == 0) {
            return 1;
        } else if (position == 2) {
            return 0;
        } else {
            return 2;
        }
    }

    //重新定位 flag代表是电脑0，2，或者是我
    public static void rePosition(JFrame main, List<JCard> list, int flag, int position) {
        Point p = new Point();
        if (flag == befor(position)) {//电脑0
            p.x = 50;
            p.y = (450 / 2) - (list.size() + 1) * 15 / 2;
        }
        if (flag == mine(position)) {//我
            p.x = (800 / 2) - (list.size() + 1) * 21 / 2;
            p.y = 450;
        }
        if (flag == next(position)) {
            p.x = 700;
            p.y = (450 / 2) - (list.size() + 1) * 15 / 2;
        }
        int len = list.size();
        for (int i = 0; i < len; i++) {
            JCard card = list.get(i);
            Common.move(card, card.getLocation(), p);
            main.getContentPane().setComponentZOrder(card, 0);
            if (flag == mine(position))
                p.x += 21;
            else
                p.y += 15;
        }
    }//移动效果的函数，用于发牌

    public static void move(JCard card, Point from, Point to) {

        if (to.x != from.x) {
            double k = (1.0) * (to.y - from.y) / (to.x - from.x);
            double b = to.y - to.x * k;
            int flag = 0;//判断是向左还是向又移动的步幅
            if (from.x < to.x) {
                flag = 20;
            } else {
                flag = -20;
            }
            for (int i = from.x; Math.abs(i - to.x) > 20; i += flag) {
                double y = k * i + b;//这里主要用的数学中的线性函数
                card.setLocation(i, (int) y);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }
        //位置校准
        card.setLocation(to);
    }

    // 设定地主
    public static void setLord(RoomPage page, int i, int postion) {
        Point point = new Point();
        if (i == mine(postion))// 我是地主
        {
            point.x = 120;
            point.y = 480;
            page.getUsers().get(i).setLordFlag(true);
        }
        if (i == 0) {
            point.x = 80;
            point.y = 20;
        }
        if (i == 2) {
            point.x = 700;
            point.y = 20;
        }
        page.getUsers().get(i).setLordFlag(true);
        page.getLord().setLocation(point);
        page.getLord().setVisible(true);
    }

}
