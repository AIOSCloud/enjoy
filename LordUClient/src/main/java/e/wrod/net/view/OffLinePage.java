package e.wrod.net.view;

import e.wrod.net.common.CardType;
import e.wrod.net.common.Common;
import e.wrod.net.common.NewTimer;
import e.wrod.net.common.Time;
import e.wrod.net.component.JCard;
import e.wrod.net.model.Card;
import lombok.Data;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

@Data
public class OffLinePage extends JFrame implements ActionListener {
    private final Logger logger = Logger.getLogger(OffLinePage.class);
    Container container = null; //面板容器
    JMenuItem start, exit, about; //界面上面的按钮
    JButton[] landlord = new JButton[2]; //抢地主，抢，不抢的按钮
    JButton[] publishCard = new JButton[2]; //出牌　出，不出的按钮
    int lordFlag; //地主标志
    int turn;
    Time t;
    JLabel lord;
    List<JCard> currentList[] = new Vector[3];
    List<JCard> playerList[] = new ArrayList[3];
    List<JCard> lordList;
    JCard jCards[] = new JCard[54];
    JTextField time[] = new JTextField[3];
    boolean nextPlayer = false;//下一个用户

    OffLinePage() {
        //界面出事化
        Init();
        // 设置菜单按钮
        setMenu();
        setPage();
        CardInit();
        washCard();
        dealCard();
        CardOrder();
        setImage();
        landLord();
        time[1].setVisible(true);
        // 设置当前面板可见
        this.setVisible(true);
        SwingUtilities.invokeLater(new NewTimer(this, 10));
    }

    public void landLord() {
        for (int i = 0; i < 2; i++) {
            landlord[i].setVisible(true);
        }
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
            landlord[i].setVisible(false);
            container.add(publishCard[i]);
            publishCard[i].setVisible(false);
            publishCard[i].addActionListener(this);
        }
        for (int i = 0; i < 3; i++) {
            time[i] = new JTextField("倒计时:");
            time[i].setVisible(false);
            container.add(time[i]);
        }
        time[0].setBounds(140, 230, 60, 20);
        time[1].setBounds(374, 360, 60, 20);
        time[2].setBounds(620, 230, 60, 20);

        for (int i = 0; i < 3; i++) {
            currentList[i] = new Vector<JCard>();
        }
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

    public void washCard() {
        //初始化之后休息三秒
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        System.out.println(jCards.length);
        //洗牌
        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            int a = random.nextInt(54);
            int b = random.nextInt(54);
            JCard k = jCards[a];
            jCards[a] = jCards[b];
            jCards[b] = k;
        }
    }

    // TODO: 2020/3/12 发牌
    public void dealCard() {
        //初始化玩家的牌
        for (int i = 0; i < 3; i++) {
            playerList[i] = new ArrayList<JCard>();
        }
        //初始化地主牌
        lordList = new Vector<JCard>();
        for (int i = 0; i < 54; i++) {
            if (i >= 51) {//地主牌
                Common.move(jCards[i], jCards[i].getLocation(), new Point(320 + (i - 51) * 80, 10));
                lordList.add(jCards[i]);
                continue;
            }
            Point point;
            switch (i % 3) {
                case 0:
                    // TODO: 2020/3/12 玩家1
                    point = new Point(50, 60 + i * 5);
                    Common.move(jCards[i], jCards[i].getLocation(), point);
                    jCards[i].turnFront();
                    playerList[0].add(jCards[i]);
                    break;
                case 1:
                    // TODO: 2020/3/12 玩家2:
                    point = new Point(180 + i * 7, 450);
                    Common.move(jCards[i], jCards[i].getLocation(), point);
                    jCards[i].turnFront();
                    playerList[1].add(jCards[i]);
                    break;
                case 2:
                    //todo 玩家3
                    point = new Point(700, 60 + i * 5);
                    Common.move(jCards[i], jCards[i].getLocation(), point);
                    jCards[i].turnFront();
                    playerList[2].add(jCards[i]);
                    break;
            }
            container.setComponentZOrder(jCards[i], 0);
        }
    }

    public void CardOrder() {
        for (int i = 0; i < 3; i++) {
            logger.debug("对玩家:" + i + " 牌排序");
            Common.order(playerList[i]);
            Common.rePosition(this, playerList[i], i);
        }
    }

    public void setImage() {
        lord = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/card/dizhu.gif")));
        lord.setVisible(false);
        lord.setSize(40, 40);
        container.add(lord);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == landlord[0]) {
            // TODO: 2020/3/13 抢地主
            System.out.println("抢地主，抢地主。。。。。。。。。。。。");
            time[1].setText("抢地主");
            t.setIsRun(false);
        }
        if (e.getSource() == landlord[1]) {
            //不抢
            time[1].setText("不抢");
            t.setIsRun(false);
        }
        if (e.getSource() == publishCard[0]) {
            logger.debug("出牌......");
            // 出牌
            List<JCard> cards = new Vector<>();
            for (int i = 0; i < playerList[1].size(); i++) {
                JCard card = playerList[1].get(i);
                if (card.isClicked()) {
                    cards.add(card);
                }
            }
            int flag = 0;
            logger.info("开始判断是否可以出牌......");
            if (time[0].getText().equals("不要") && time[2].getText().equals("不要")) {
                logger.debug("判断需要出牌的类型:" + Common.jugdeType(cards));
                if (Common.jugdeType(cards) != CardType.c0)
                    flag = 1;
            } else {
                flag = Common.checkCards(cards, currentList);
            }
            //可以出牌
            logger.debug("判断是否可以出牌:" + flag);
            logger.debug("当前自己出牌的数量:" + currentList[1].size());
            if (flag == 1) {
                currentList[1] = cards;
                playerList[1].removeAll(cards);
                //定位出牌
                Point point = new Point();
                point.x = (770 / 2) - (currentList[1].size() + 1) * 15 / 2;
                point.y = 300;
                for (int i = 0, len = currentList[1].size(); i < len; i++) {
                    JCard card = currentList[1].get(i);
                    Common.move(card, card.getLocation(), point);
                    point.x += 15;
                }
                //重新理牌
                Common.rePosition(this, playerList[1], 1);
                time[1].setVisible(false);
                this.nextPlayer = true;
                publishCard[0].setVisible(false);
                publishCard[1].setVisible(false);
            }
        }
        if (e.getSource() == publishCard[1]) {
            this.nextPlayer = true;
            currentList[1].clear();
            time[1].setText("不要");
        }

    }

    public static void main(String[] args) {
        new OffLinePage();
    }
}
