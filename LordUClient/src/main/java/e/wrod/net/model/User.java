package e.wrod.net.model;

import e.wrod.net.common.Common;
import e.wrod.net.component.JCard;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息
 */
@Data
public class User {
    //用户Id
    private int userId;
    //用户名称
    private String userName;
    //用户密码
    private String password;
    //是否抢地主
    private boolean lord;
    //地主标签
    private boolean lordFlag;
    private int lordRole;
    //是否轮到自己出牌
    private boolean turn;
    private boolean follow;
    //是否托管 --交给电脑，按照电脑算法出牌
    private boolean deposit;
    //用户牌
    List<JCard> players;
    //用户出的牌
    List<JCard> shows;
    boolean pass;
    int score;

    public User() {
        players = new ArrayList<>();
        shows = new ArrayList<>();
    }

    public void addCard(JCard card) {
        players.add(card);
    }

    public void orders() {
        Common.order(players);
    }
}
