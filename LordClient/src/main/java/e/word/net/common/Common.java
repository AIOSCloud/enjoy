package e.word.net.common;

import e.word.net.component.JCard;
import e.word.net.model.Card;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Common {
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

    //移动效果的函数，用于发牌
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

    //重新定位 flag代表是电脑0，2，或者是我
    public static void rePosition(JFrame main, List<JCard> list, int flag) {
        Point p = new Point();
        if (flag == 0) {//电脑0
            p.x = 50;
            p.y = (450 / 2) - (list.size() + 1) * 15 / 2;
        }
        if (flag == 1) {//我
            p.x = (800 / 2) - (list.size() + 1) * 21 / 2;
            p.y = 450;
        }
        if (flag == 2) {
            p.x = 700;
            p.y = (450 / 2) - (list.size() + 1) * 15 / 2;
        }
        int len = list.size();
        for (int i = 0; i < len; i++) {
            JCard card = list.get(i);
            Common.move(card, card.getLocation(), p);
            main.getContentPane().setComponentZOrder(card, 0);
            if (flag == 1)
                p.x += 21;
            else
                p.y += 15;
        }
    }

    public static List<JCard> getJCards(List<JCard> players, List<Card> cards) {
        List<JCard> jCards = new ArrayList<>(cards.size());
        for (Card card : cards) {
            JCard jCard = new JCard(card, true);
        }
        return jCards;
    }

    public static List<Card> getCards(List<JCard> jCards) {
        List<Card> cards = new ArrayList<>();
        for (JCard jCard : jCards) {
            cards.add(jCard.getCard());
        }
        return cards;
    }
}
