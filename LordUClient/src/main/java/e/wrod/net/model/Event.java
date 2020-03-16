package e.wrod.net.model;

import e.wrod.net.component.JCard;

import java.util.List;

/**
 * 消息通信
 */
public class Event {
    // TODO: 用户位置
    int position;
    //轮到谁出牌
    int turn;
    //跟谁的牌
    int follow;
    List<JCard> show;
}
