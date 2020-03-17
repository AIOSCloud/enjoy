package e.word.net.common;

import e.word.net.model.Card;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Common {

    private static Logger logger = Logger.getLogger(Common.class);

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

    //判断牌型
    public static CardType jugdeType(List<Card> list) {
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
            if (len == 2 && list.get(1).getColor() == 5)
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

    //返回花色
    public static int getColor(Card card) {
        return card.getColor();
    }

    //得到最大相同数
    public static void getMax(CardIndex cardIndex, List<Card> list) {
        int count[] = new int[14];//1-13各算一种,王算第14种
        for (int i = 0; i < 14; i++)
            count[i] = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (getColor(list.get(i)) == 5)
                count[13]++;
            else
                count[Common.getValue(list.get(i)) - 1]++;
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

    //返回值
    public static int getValue(Card card) {
        int i = card.getNumber();
        if (i == 2)
            i += 13;
        if (i == 1)
            i += 13;
        if (card.getColor() == 5)
            i += 2;//是王
        return i;
    }
}
