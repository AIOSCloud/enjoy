package e.word.net.model;

import lombok.Data;

import javax.xml.ws.soap.MTOMFeature;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Data
public class Room {
    // TODO: 2020/3/16 卡片 
    Card[] cards = new Card[54];
    // TODO: 2020/3/16 房间用户 
    private List<User> users;
    // TODO: 2020/3/16 出牌
    List<Card> showsList[] = new Vector[3];
    // TODO: 2020/3/16 玩家牌
    List<Card> playerList[] = new ArrayList[3];

    public Room() {

    }
}
