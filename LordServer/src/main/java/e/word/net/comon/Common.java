package e.word.net.comon;

import e.word.net.model.Card;
import org.apache.log4j.Logger;

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

    //地主牌权值，看是否抢地主
    public static int getScore(List<Card> list) {
        int count = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            Card card = list.get(i);
            if (card.getColor() == 5) {
                count += 5;
            }
            if (card.getNumber() == 2) {
                count += 2;
            }

        }
        return count;
    }
}
