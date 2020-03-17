package e.word.net.model;

import e.word.net.comon.Common;
import lombok.Data;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class Room {
    private Logger logger = Logger.getLogger(Room.class);
    // TODO: 2020/3/16 卡片
    Card[] cards = new Card[54];
    // TODO: 2020/3/16 房间用户 
    ConcurrentMap<Integer, User> users;
    // TODO: 2020/3/16 出牌
    List<Card>[] showsList = new ArrayList[3];
    // TODO: 2020/3/16 玩家牌
    List<Card> playerList[] = new ArrayList[3];
    //地主牌
    List<Card> lordCards;

    public Room() {
        lordCards = new ArrayList<>(3);
        users = new ConcurrentHashMap<>(3);
        for (int i = 0; i < 3; i++) {
            showsList[i] = new ArrayList<Card>();
            playerList[i] = new ArrayList<Card>();
        }
        // TODO: 2020/3/16 牌面初始化
        int count = 0;
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 13; j++) {
                if ((i == 5) && (j > 2)) {
                    break;
                } else {
                    Card card = new Card(i, j);
                    cards[count] = card;
                    count++;
                }
            }
        }
    }

    public void washCard() {
        //洗牌
        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            int a = random.nextInt(54);
            int b = random.nextInt(54);
            Card k = cards[a];
            cards[a] = cards[b];
            cards[b] = k;
        }
    }

    public void dealCard() {
        //初始化地主牌
        for (int i = 0; i < 54; i++) {
            if (i >= 51) {//地主牌
                lordCards.add(cards[i]);
                continue;
            }
            Point point;
            switch (i % 3) {
                case 0:
                    playerList[0].add(cards[i]);
                    break;
                case 1:
                    playerList[1].add(cards[i]);
                    break;
                case 2:
                    //todo 玩家3
                    playerList[2].add(cards[i]);
                    break;
            }
        }
    }

    public void orders() {
        for (int i = 0; i < 3; i++) {
            logger.debug("对玩家:" + i + " 牌排序");
            Common.order(playerList[i]);
        }
    }
}
