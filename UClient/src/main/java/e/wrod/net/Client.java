package e.wrod.net;

import e.wrod.net.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Client extends JFrame implements ActionListener {
    public Container container = null; //面板容器
    JMenuItem start, exit, about; //界面上面的按钮
    JButton landlord[] = new JButton[2]; //抢地主，抢，不抢的按钮
    JButton publishCard[] = new JButton[2]; //出牌　出，不出的按钮
    int isLord; //地主标志
    int turn;
    JLabel lord;
    List<Card> currentList[] = new ArrayList[3];
    List<Card> playerList[] = new ArrayList[3];
    List<Card> lordList;
    Card card[] = new Card[56];
    JTextField time[] = new JTextField[3];

    public Client() {
        //界面出事化
        Init();
        // 设置菜单按钮
        setMenu();
        // 设置当前面板可见
        this.setVisible(true);
        //开始,发牌
        CardInit();
        //抢地主
        getLord();
        // TODO: 2020/3/10 计时器
    }

    public static void main(String[] args) {
        new Client();
    }

    //界面初始化
    public void Init() {
        this.setTitle("互联世界出品");
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
        JMenu game = new JMenu("游戏");
        JMenu help = new JMenu("帮助");
        start = new JMenuItem("游戏开始");
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

        landlord[0] = new JButton("抢地主");
        landlord[1] = new JButton("不 抢");
        publishCard[0] = new JButton("出牌");
        publishCard[1] = new JButton("过");
        for (int i = 0; i < 2; i++) {
            publishCard[i].setBounds(320 + i * 100, 400, 60, 20);
            landlord[i].setBounds(320 + i * 100, 400, 75, 20);
            container.add(landlord[i]);
            landlord[i].addActionListener(this);
            landlord[i].setVisible(false);
            container.add(publishCard[i]);
            publishCard[i].setVisible(false);
            publishCard[i].addActionListener(this);
        }
        for (int i = 0; i < 3; i++) {
            time[i] = new JTextField(" 倒计时:");
            time[i].setVisible(false);
            container.add(time[i]);
        }
        time[0].setBounds(140, 230, 60, 20);
        time[1].setBounds(374, 360, 60, 20);
        time[2].setBounds(620, 230, 60, 20);

        for (int i = 0; i < 3; i++) {
            currentList[i] = new ArrayList<Card>();
        }
    }

    // TODO: 2020/3/10 卡片初始化
    public void CardInit() {
    }

    // TODO: 2020/3/10　抢地主
    public void getLord() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
