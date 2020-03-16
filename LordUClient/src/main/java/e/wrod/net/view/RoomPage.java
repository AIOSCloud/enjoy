package e.wrod.net.view;

import e.wrod.net.common.UTimer;
import e.wrod.net.component.JCard;
import e.wrod.net.model.User;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

//房间
@Data
public class RoomPage extends JFrame {
    User[] user;
    int mine = 0;
    int befor;
    int next;
    Container container = null; //面板容器
    JMenuItem start, exit, about; //界面上面的按钮
    JButton[] landlord = new JButton[2]; //抢地主，抢，不抢的按钮
    JButton[] publishCard = new JButton[2]; //出牌　出，不出的按钮
    //地主标签
    JLabel lord;
    JCard jCards[] = new JCard[54];
    JTextField time[] = new JTextField[3];
    UTimer timer;
    List<JCard> currentList[] = new Vector[3];
    List<JCard> playerList[] = new ArrayList[3];
    //地主牌
    List<JCard> lordList;
    int lordFlag; //地主标志
    int turn;
    int follow;
    Map<Integer, Boolean> loadLord = new HashMap<>();
}
