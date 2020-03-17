package e.word.net.common;

import e.word.net.model.Card;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Compute {
    private static Logger logger = Logger.getLogger(Compute.class);

    //拆牌
    public static Model getModel(List<Card> list) {
        logger.debug("牌分类......");
        //先复制一个list
        List list2 = new ArrayList<Card>(list);
        Model model = new Model();
        //------先拆炸弹
        Compute.getBoomb(list2, model); //ok
        printModel(model.getA4());
        //------拆3带
        Compute.getThree(list2, model);
        printModel(model.getA3());
        //拆飞机
        Compute.getPlane(list2, model);
        printModel(model.getA111222());
        //------拆对子
        Compute.getTwo(list2, model);
        printModel(model.getA2());
        //拆连队
        Compute.getTwoTwo(list2, model);
        printModel(model.getA112233());
        //拆顺子
        Compute.get123(list2, model);
        printModel(model.getA123());
        //拆单
        Compute.getSingle(list2, model);
        printModel(model.getA1());
        return model;
    }

    public static void printModel(List<List<Card>> model) {
        int count = 0;
        for (List<Card> cards : model) {
            logger.debug("拆牌:" + count);
            for (Card card : cards) {
                logger.debug("拆牌:" + card.getColor() + "-" + card.getNumber());
            }
            count++;
        }
    }

    //拆连子
    public static void get123(List<Card> list, Model model) {
        List<Card> del = new ArrayList<Card>();//要删除的Cards
        List<Card> cards;
        if (list.size() > 0 && (Common.getValue(list.get(0)) < 7 || Common.getValue(list.get(list.size() - 1)) > 10))
            return;
        if (list.size() < 5)
            return;
        for (int i = 0, len = list.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (Common.getValue(list.get(i)) - Common.getValue(list.get(j)) == j - i) {
                    k = j;
                }
            }
            if (k - i >= 4) {
                cards = new ArrayList<>();
                for (int j = i; j <= k; j++) {
                    cards.add(list.get(j));
                    del.add(list.get(j));
                }
                model.getA123().add(cards);
                i = k;
            }
        }
        list.removeAll(del);
    }

    //拆双顺
    public static void getTwoTwo(List<Card> list, Model model) {
        List<List<Card>> del = new ArrayList<List<Card>>();//要删除的Cards
        List<Card> two2;
        //从model里面的对子找
        List<List<Card>> twos = model.getA2();
        List<Card> cards;
        if (twos.size() < 3)
            return;
        Card[] card = new Card[twos.size()];
        for (int i = 0, len = twos.size(); i < len; i++) {
            cards = twos.get(i);
            card[i] = cards.get(0);
        }
        //s0,1,2,3,4  13,9,8,7,6
        for (int i = 0, len = twos.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (Common.getValue(card[i]) - Common.getValue(card[j]) == j - i)
                    k = j;
            }
            if (k - i >= 2)//k=4 i=1
            {//说明从i到k是连队
                two2 = new ArrayList<>();
                for (int j = i; j <= k; j++) {
                    two2.addAll(twos.get(j));
                    del.add(twos.get(j));
                }
                model.getA112233().add(two2);
                del.add(twos.get(k));
                i = k;
            }
        }
        twos.removeAll(del);
    }

    //拆飞机
    public static void getPlane(List<Card> list, Model model) {
        List<List<Card>> del = new ArrayList<List<Card>>();//要删除的Cards
        //从model里面的3带找
        List<List<Card>> threes = model.getA3();
        List<Card> cards;
        List<Card> plans;
        if (threes.size() < 2)
            return;
        Card[] card = new Card[threes.size()];
        for (int i = 0, len = threes.size(); i < len; i++) {
            cards = threes.get(i);
            card[i] = cards.get(0);
        }
        for (int i = 0, len = threes.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (Common.getValue(card[j]) - Common.getValue(card[i]) == j - i)
                    k = j;
            }
            if (k != i) {//说明从i到k是飞机
                plans = new ArrayList<>(2);
                for (int j = i; j <= k; j++) {
                    plans.addAll(threes.get(j));
                    del.add(threes.get(j));
                }
                model.getA111222().add(plans);
                i = k;
            }
        }
        threes.removeAll(del);
    }

    //拆炸弹
    public static void getBoomb(List<Card> list, Model model) {
        logger.debug("牌型分类炸弹");
        List<Card> del = new ArrayList<Card>();//要删除的Cards
        List<Card> boom = null;
        //王炸
        if (list.size() >= 2 && list.get(0).getColor() == 5 && list.get(1).getColor() == 5) {
            boom = new ArrayList<>(2);
            boom.add(list.get(0));
            boom.add(list.get(1));
            model.getA4().add(boom);
            del.add(list.get(0));
            del.add(list.get(1));
        }
        list.removeAll(del);
        //一般的炸弹
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 3 < len && Common.getValue(list.get(i)) == Common.getValue(list.get(i + 3))) {
                boom = new ArrayList<>(4);
                for (int j = i; j <= i + 3; j++) {
                    boom.add(list.get(j));
                    del.add(list.get(j));
                }
                model.getA4().add(boom);
                i = i + 3;
            }
        }
        list.removeAll(del);
    }

    //拆3带
    public static void getThree(List<Card> list, Model model) {
        logger.debug("牌型分类三带");
        List<Card> del = new ArrayList<Card>();//要删除的Cards
        List<Card> three;
        //连续3张相同
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 2 < len && Common.getValue(list.get(i)) == Common.getValue(list.get(i + 2))) {
                three = new ArrayList<>(3);
                for (int j = i; j <= i + 2; j++) {
                    three.add(list.get(j));
                    del.add(list.get(j));
                }
                model.getA123().add(three);
                i = i + 2;
            }
        }
        list.removeAll(del);
    }

    //拆对子
    public static void getTwo(List<Card> list, Model model) {
        logger.debug("牌型分类对子");
        List<Card> del = new ArrayList<Card>();//要删除的Cards
        List<Card> two;
        //连续2张相同
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 1 < len && Common.getValue(list.get(i)) == Common.getValue(list.get(i + 1))) {
                two = new ArrayList<>(2);
                two.add(list.get(i));
                two.add(list.get(i + 1));
                del.add(list.get(i));
                del.add(list.get(i + 1));
                model.getA2().add(two);
                i = i + 1;
            }
        }
        list.removeAll(del);
    }

    //拆单牌
    public static void getSingle(List<Card> list, Model model) {
        logger.debug("牌型分类单牌");
        List<Card> del = new ArrayList<Card>();//要删除的Cards
        List<Card> single;
        for (int i = 0, len = list.size(); i < len; i++) {
            single = new ArrayList<>();
            single.add(list.get(i));
            model.getA1().add(single);
            del.add(list.get(i));
        }
        list.removeAll(del);
    }
}
