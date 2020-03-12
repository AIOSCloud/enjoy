package e.wrod.net.common;

import e.wrod.net.component.JCard;

import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class Common {

    //对list排序
    public static void order(List<JCard> list) {
        list.sort(new Comparator<JCard>() {
            @Override
            public int compare(JCard c1, JCard c2) {
                return weight(c1) - weight(c2);
            }
        });
    }

    public static int weight(JCard card) {
        int color = card.getCard().getColor();
        int number = card.getCard().getNumber();
        //如果是王的话
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
}

class Card_index {
    List a[] = new Vector[4];

}
