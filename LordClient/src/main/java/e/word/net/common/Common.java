package e.word.net.common;

import e.word.net.component.JCard;
import e.word.net.model.Card;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Common {
    //排序
    public static void order(List<Card> list) {
        list.sort(new Comparator<Card>() {
            @Override
            public int compare(Card c1, Card c2) {
                return weight(c2) - weight(c1);
            }
        });
    }

    public static int weight(Card card) {
        int color = card.getColor();
        int number = card.getNumber();
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

    public static List<JCard> getJCards(List<Card> cards) {
        List<JCard> jCards = new ArrayList<>(cards.size());
        for (Card card : cards) {
            JCard jCard = new JCard(card, true);
            // TODO: 2020/3/17 设置可点击
            jCards.add(jCard);
        }
        return jCards;
    }
}
