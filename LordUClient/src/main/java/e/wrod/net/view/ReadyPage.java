package e.wrod.net.view;

import e.wrod.net.model.Room;
import e.wrod.net.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 准备
 */
public class ReadyPage extends JFrame implements ActionListener {
    private User user;
    private Room room;
    private Container container;
    JMenuItem start, exit, about; //
    JLabel image_label, title_label, seat_label;
    JLabel lb_user1, lb_user2, lb_user3;
    JPanel center_panel;
    JPanel[] user_panel = new JPanel[3];

    ReadyPage(User user, Room room) {
        this.user = user;
        this.room = room;
        Init();
        setMenu();
        setPage();
        this.setVisible(true);
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

    //界面初始化
    public void Init() {
        this.setTitle("互联世界");
        this.setSize(1024, 548);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        container = this.getContentPane();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setPage() {
        // 顶部标题
        title_label = new JLabel("互联世界，世界互联", JLabel.CENTER);
        title_label.setSize(1024, 300);
        title_label.setBackground(Color.DARK_GRAY);
        //中间板块
        lb_user1 = new JLabel("用户");
        lb_user2 = new JLabel("用户");
        lb_user3 = new JLabel("用户");
        center_panel = new JPanel();
        center_panel.setLayout(new GridLayout(1, 3));
        user_panel[0] = new JPanel();
        user_panel[0].setBackground(Color.BLACK);
        user_panel[0].add(lb_user1);
        user_panel[1] = new JPanel();
        user_panel[1].add(lb_user2);
        user_panel[1].setBackground(Color.BLUE);
        user_panel[2] = new JPanel();
        user_panel[2].add(lb_user3);
        user_panel[2].setBackground(Color.GRAY);
        center_panel.setSize(600, 200);
        center_panel.add(user_panel[0], "East");
        center_panel.add(user_panel[1], "Center");
        center_panel.add(user_panel[2], "West");
        container.add(title_label, "North");
        container.add(center_panel, "Center");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        new ReadyPage(null, null);
    }
}
