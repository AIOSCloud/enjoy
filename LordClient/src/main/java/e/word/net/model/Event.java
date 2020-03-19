package e.word.net.model;

import lombok.Data;

import java.util.List;

@Data
public class Event {
    // TODO: 2020/3/18 数据类型
    String type;
    // TODO: 2020/3/18 通信状态
    int status;
    //地主位置
    int lordIndex;
    // TODO: 2020/3/18 轮到出牌的位置
    int turn;
    //当前用户的位置
    int index;
    //单机 或者 在线对战
    boolean online;
    //是否抢地主
    boolean lord;
    //用户信息
    User user;
    // 用户信息
    List<User> users;
    // 地主牌
    List<Card> lordList;
    // TODO: 2020/3/17 用户牌
    List<Card> players;
    // TODO: 2020/3/17 用户出牌
    List<Card> shows;
    int showIndex;
    //出牌上家信息
    int lastIndex;
    boolean play;
    int playIndex;
}