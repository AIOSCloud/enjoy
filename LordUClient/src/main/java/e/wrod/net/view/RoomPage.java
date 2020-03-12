package e.wrod.net.view;

import e.wrod.net.component.JCard;
import e.wrod.net.model.Card;
import e.wrod.net.model.Room;
import e.wrod.net.model.User;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 房间
 */
public class RoomPage extends JFrame implements ActionListener {
    private final Logger logger = Logger.getLogger(HomePage.class);
    private User user;
    private Room room;
    private Container container = null; //面板容器
    JMenuItem start, exit, about; //界面上面的按钮
    JButton[] landlord = new JButton[2]; //抢地主，抢，不抢的按钮
    JButton[] publishCard = new JButton[2]; //出牌　出，不出的按钮
    int isLord; //地主标志
    int turn;
    JLabel lord;
    ArrayList[] currentList = new ArrayList[3];
    ArrayList[] playerList = new ArrayList[3];
    List<Card> lordList;
    JCard jCards[] = new JCard[56];
    JTextField time[] = new JTextField[3];
    private static Image image;

    RoomPage(User user, Room room) {
        this.user = user;
        this.room = room;
        //界面出事化
        Init();
        // 设置菜单按钮
        setMenu();
        setPage();
        CardInit();
        // 设置当前面板可见
        this.setVisible(true);
    }

    //界面初始化
    public void Init() {
        this.setTitle("互联世界");
        this.setSize(830, 620);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        container = this.getContentPane();
        container.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container.setBackground(new Color(0, 112, 26));
    }

    /**
     * 菜单按钮布局
     */
    public void setMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu game = new JMenu("开始");
        JMenu help = new JMenu("帮助");
        start = new JMenuItem("开始");
        exit = new JMenuItem("退出");
        about = new JMenuItem("关于");
        start.addActionListener(this);
        exit.addActionListener(this);
        about.addActionListener(this);
        game.add(start);
        game.add(exit);
        help.add(about);
        jMenuBar.add(game);
        jMenuBar.add(help);
        this.setJMenuBar(jMenuBar);
    }

    public void setPage() {
        landlord[0] = new JButton("抢地主");
        landlord[1] = new JButton("不 抢");
        publishCard[0] = new JButton("出牌");
        publishCard[1] = new JButton("不要");
        for (int i = 0; i < 2; i++) {
            publishCard[i].setBounds(320 + i * 100, 400, 60, 20);
            landlord[i].setBounds(320 + i * 100, 400, 75, 20);
            container.add(landlord[i]);
            landlord[i].addActionListener(this);
            landlord[i].setVisible(true);
            container.add(publishCard[i]);
            publishCard[i].setVisible(true);
            publishCard[i].addActionListener(this);
        }
        for (int i = 0; i < 3; i++) {
            time[i] = new JTextField("倒计时:");
            time[i].setVisible(true);
            container.add(time[i]);
        }
        time[0].setBounds(140, 230, 60, 20);
        time[1].setBounds(374, 360, 60, 20);
        time[2].setBounds(620, 230, 60, 20);
    }

    // TODO: 2020/3/10 卡片初始化
    public void CardInit() {
        //初始化牌
        int count = 0;
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 13; j++) {
                if ((i == 5) && (j > 2)) {
                    break;
                } else {
                    Card card = new Card(i, j);
                    jCards[count] = new JCard(card, false);
                    jCards[count].setLocation(350, 50);
                    container.add(jCards[count]);
                    count++;
                }
            }
        }
    }

    // TODO: 2020/3/10　抢地主
    public void getLord() {
        for (int i = 0; i < 2; i++)
            landlord[i].setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
        }
        if (e.getSource() == about) {
            //关于
        }
        if (e.getSource() == exit) {
            //退出
            this.dispose();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new RoomPage(null, null);
    }
}
