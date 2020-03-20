package e.word.net.common;

import e.word.net.component.JCard;
import e.word.net.model.Card;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Common {
    private static Logger logger = Logger.getLogger(Common.class);

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

    //判断牌型
    public static CardType jugdeType(List<JCard> list) {
        //因为之前排序过所以比较好判断
        int len = list.size();
        //单牌,对子，3不带，4个一样炸弹
        if (len <= 4) {    //如果第一个和最后个相同，说明全部相同
            if (list.size() > 0 && Common.getValue(list.get(0)) == Common.getValue(list.get(len - 1))) {
                switch (len) {
                    case 1:
                        return CardType.c1;
                    case 2:
                        return CardType.c2;
                    case 3:
                        return CardType.c3;
                    case 4:
                        return CardType.c4;
                }
            }
            //双王,化为对子返回
            if (len == 2 && Common.getColor(list.get(1)) == 5)
                return CardType.c2;
            //当第一个和最后个不同时,3带1
            if (len == 4 && ((Common.getValue(list.get(0)) == Common.getValue(list.get(len - 2))) ||
                    Common.getValue(list.get(1)) == Common.getValue(list.get(len - 1))))
                return CardType.c31;
            else {
                return CardType.c0;
            }
        }
        //当5张以上时，连字，3带2，飞机，2顺，4带2等等
        if (len >= 5) {//现在按相同数字最大出现次数
            CardIndex cardIndex = new CardIndex();
            for (int i = 0; i < 4; i++)
                cardIndex.index[i] = new ArrayList<Integer>();
            //求出各种数字出现频率
            Common.getMax(cardIndex, list); //a[0,1,2,3]分别表示重复1,2,3,4次的牌
            //3带2 -----必含重复3次的牌
            if (cardIndex.index[2].size() == 1 && cardIndex.index[1].size() == 1 && len == 5)
                return CardType.c32;
            //4带2(单,双)
            if (cardIndex.index[3].size() == 1 && len == 6)
                return CardType.c411;
            if (cardIndex.index[3].size() == 1 && cardIndex.index[1].size() == 2 && len == 8)
                return CardType.c422;
            //单连,保证不存在王
            if ((Common.getColor(list.get(0)) != 5) && (cardIndex.index[0].size() == len) &&
                    (Common.getValue(list.get(0)) - Common.getValue(list.get(len - 1)) == len - 1))
                return CardType.c123;
            //连队
            if (cardIndex.index[1].size() == len / 2 && len % 2 == 0 && len / 2 >= 3
                    && (Common.getValue(list.get(0)) - Common.getValue(list.get(len - 1)) == (len / 2 - 1)))
                return CardType.c1122;
            //飞机
            if (cardIndex.index[2].size() == len / 3 && (len % 3 == 0) &&
                    (Common.getValue(list.get(0)) - Common.getValue(list.get(len - 1)) == (len / 3 - 1)))
                return CardType.c111222;
            //飞机带n单,n/2对
            if (cardIndex.index[2].size() == len / 4 &&
                    ((Integer) (cardIndex.index[2].get(len / 4 - 1)) - (Integer) (cardIndex.index[2].get(0)) == len / 4 - 1))
                return CardType.c11122234;

            //飞机带n双
            if (cardIndex.index[2].size() == len / 5 && cardIndex.index[2].size() == len / 5 &&
                    ((Integer) (cardIndex.index[2].get(len / 5 - 1)) - (Integer) (cardIndex.index[2].get(0)) == len / 5 - 1))
                return CardType.c1112223344;

        }
        return CardType.c0;
    }


    //返回值
    public static int getValue(JCard card) {
        int i = card.getCard().getNumber();
        if (i == 2)
            i += 13;
        if (i == 1)
            i += 13;
        if (card.getCard().getColor() == 5)
            i += 2;//是王
        return i;
    }


    //返回花色
    public static int getColor(JCard card) {
        return card.getCard().getColor();
    }


    //得到最大相同数
    public static void getMax(CardIndex cardIndex, List<JCard> list) {
        int count[] = new int[14];//1-13各算一种,王算第14种
        for (int i = 0; i < 14; i++)
            count[i] = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (getColor(list.get(i)) == 5) {
                count[13]++;
            } else {
                count[Common.getValue(list.get(i)) - 1]++;
            }
        }
        for (int i = 0; i < 14; i++) {
            switch (count[i]) {
                case 1:
                    cardIndex.getIndex()[0].add(i + 1);
                    break;
                case 2:
                    cardIndex.getIndex()[1].add(i + 1);
                    break;
                case 3:
                    cardIndex.getIndex()[2].add(i + 1);
                    break;
                case 4:
                    cardIndex.getIndex()[3].add(i + 1);
                    break;
            }
        }
    }


    //检查牌的是否能出
    public static int checkCards(List<JCard> cards, List<JCard> currentlist) {
        logger.debug("检查是否可以出牌");
        CardType cType = Common.jugdeType(cards);
        logger.debug("判断需要出牌的类型:" + cType);
        //如果张数不同直接过滤
        if (cType != CardType.c4 && cards.size() != currentlist.size()) {
            logger.debug("出牌张数不对......");
            return 0;
        }
        //比较我的出牌类型
        if (Common.jugdeType(cards) != Common.jugdeType(currentlist)) {
            logger.debug("出的牌比较小......");
            return 0;
        }
        //比较出的牌是否要大
        //王炸弹
        if (cType == CardType.c4) {
            if (cards.size() == 2)
                return 1;
            if (currentlist.size() == 2)
                return 0;
        }
        //单牌,对子,3带,4炸弹
        if (cType == CardType.c1 || cType == CardType.c2 || cType == CardType.c3 || cType == CardType.c4) {
            logger.debug("Common.getValue(cards.get(0))" + Common.getValue(cards.get(0)) + "    Common.getValue(currentlist.get(0)):" + Common.getValue(currentlist.get(0)));
            if (Common.getValue(cards.get(0)) <= Common.getValue(currentlist.get(0))) {
                return 0;
            } else {
                return 1;
            }
        }
        //顺子,连队，飞机裸
        if (cType == CardType.c123 || cType == CardType.c1122 || cType == CardType.c111222) {
            if (Common.getValue(cards.get(0)) <= Common.getValue(currentlist.get(0)))
                return 0;
            else
                return 1;
        }
        //按重复多少排序
        //3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的
        if (cType == CardType.c31 || cType == CardType.c32 || cType == CardType.c411 || cType == CardType.c422
                || cType == CardType.c11122234 || cType == CardType.c1112223344) {
            Common.order(cards); //我出的牌
            Common.order(currentlist);//当前最大牌
            if (Common.getValue(cards.get(0)) < Common.getValue(currentlist.get(0)))
                return 0;
        }
        return 1;
    }
}
