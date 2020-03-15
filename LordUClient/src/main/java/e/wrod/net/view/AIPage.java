package e.wrod.net.view;

import e.wrod.net.common.Common2;
import e.wrod.net.common.NewTimer2;
import e.wrod.net.common.UTimer;
import e.wrod.net.component.JCard;
import e.wrod.net.model.Card;
import e.wrod.net.model.User;
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
public class AIPage extends RoomPage implements ActionListener {
    Logger logger = Logger.getLogger(AIPage.class);

    AIPage(User user, List<User> users) {
        //用户信息初始化
        this.user = user;
        this.users = users;
        for (int i = 0; i < this.users.size(); i++) {
            if (user.getUserName().equals(this.users.get(i).getUserName())) {
                position = i;
            }
        }
        logger.debug("用户位置:" + position);
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
        SwingUtilities.invokeLater(new NewTimer2(this, position));
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
    }

    // TODO: 2020/3/10 卡片初始化
    public void CardInit() {
        int count = 0;
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 13; j++) {
                if ((i == 5) && (j > 2)) {
                    break;
                } else {
                    Card card = new Card(i, j);
                    jCards[count] = new JCard(card, false);
                    jCards[count].setLocation(350 + i * 5, 50);
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

    // TODO: 2020/3/12 发牌 如果要考虑 当前客户端用户呢？如何处理呢？
    public void dealCard() {
        //初始化地主牌
        lordList = new Vector<JCard>();
        for (int i = 0; i < 54; i++) {
            if (i >= 51) {//地主牌
                Common2.move(jCards[i], jCards[i].getLocation(), new Point(320 + (i - 51) * 80, 10));
                lordList.add(jCards[i]);
                continue;
            }
            Point point;
            switch (i % 3) {
                case 0:
                    // TODO: 2020/3/12 玩家1
                    point = new Point(50, 60 + i * 5);
                    Common2.move(jCards[i], jCards[i].getLocation(), point);
                    //发牌
                    users.get(Common2.befor(position)).addCard(jCards[i]);
                    break;
                case 1:
                    // TODO: 2020/3/12 玩家2:
                    point = new Point(180 + i * 7, 450);
                    Common2.move(jCards[i], jCards[i].getLocation(), point);
                    jCards[i].turnFront();
                    users.get(Common2.mine(position)).addCard(jCards[i]);
                    break;
                case 2:
                    //todo 玩家3
                    point = new Point(700, 60 + i * 5);
                    Common2.move(jCards[i], jCards[i].getLocation(), point);
                    users.get(Common2.next(position)).addCard(jCards[i]);
                    break;
            }
            container.setComponentZOrder(jCards[i], 0);
        }
    }

    public void CardOrder() {
        for (int i = 0; i < 3; i++) {
            logger.debug("对玩家:" + i + " 牌排序");
            Common2.order(users.get(i).getPlayers());
            Common2.rePosition(this, users.get(i).getPlayers(), i, position);
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
            logger.debug("抢地主，抢地主。。。。。。。。。。。。");
            users.get(position).setLord(true);
            users.get(position).setLordFlag(true);
            time[1].setText("抢地主");
            timer.setLoadLord(false);
        }
        if (e.getSource() == landlord[1]) {
            //不抢
            time[1].setText("不抢");
            timer.setLoadLord(false);
        }
        if (e.getSource() == publishCard[0]) {
            // TODO: 2020/3/15 出牌 
            logger.debug("出牌");
        }
        if (e.getSource() == publishCard[1]) {
            // TODO: 2020/3/15 不要 
        }

    }

    public static void main(String[] args) {
        List<User> users = new ArrayList<>(3);
        User user = new User();
        //初始化AI1
        user.setUserId(0);
        user.setUserName("AI1");
        user.setDeposit(false);
        users.add(user);
        //初始化AI2
        User user1 = new User();
        user1.setUserId(1);
        user1.setUserName("徐昌");
        user1.setDeposit(false);
        users.add(user1);
        //初始化AI3
        User user2 = new User();
        user2.setUserId(2);
        user2.setUserName("AI1");
        user2.setDeposit(false);
        users.add(user2);

        User user0 = new User();
        user0.setUserId(0);
        user0.setUserName("徐昌");
        user0.setDeposit(false);
        new AIPage(user0, users);
    }
}
