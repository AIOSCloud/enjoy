package e.word.net.model;

import lombok.Data;

@Data
public class Card {
    int color;//片面花色
    int number;//牌面数字

    public Card() {

    }

    public Card(int color, int number) {
        this.color = color;
        this.number = number;
    }
}
