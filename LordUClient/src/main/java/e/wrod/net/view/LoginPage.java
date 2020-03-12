package e.wrod.net.view;

import com.alibaba.fastjson.JSON;
import e.wrod.net.model.Message;
import e.wrod.net.model.User;
import e.wrod.net.utils.MyWebSocketClient;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录首页
 */
public class LoginPage extends JFrame implements ActionListener {
    //定义北部需要的组件

    JLabel jbl1;
    //定义中部需要的组件
    //.中部有三个JPanel,有一个叫选项卡窗口管理
    JTabbedPane jtp;
    JPanel jp2, jp3, jp4;
    JLabel jp2_jbl1, jp2_jbl2, jp2_jbl3, jp2_jbl4;
    JButton jp2_jb1;
    JTextField jp2_jtf;
    JPasswordField jp2_jpf;
    JCheckBox jp2_jcb1, jp2_jcb2;
    //定义南部需要的组件
    JPanel jp1;
    JButton jp1_jb1, jp1_jb2, jp1_jb3;
    MyWebSocketClient ws;

    public LoginPage(MyWebSocketClient ws) {
        this.ws = ws;
        //处理北部
        //jbl1 = new JLabel(new ImageIcon(ClassLoader.getSystemResource("image/tou.gif")));
        jbl1 = new JLabel("互联世界，世界互联", JLabel.CENTER);
        //处理中部
        jp2 = new JPanel(new GridLayout(3, 3));

        jp2_jbl1 = new JLabel("账号", JLabel.CENTER);
        jp2_jbl2 = new JLabel("密码", JLabel.CENTER);
        jp2_jbl3 = new JLabel("忘记密码", JLabel.CENTER);
        jp2_jbl3.setForeground(Color.blue);
        jp2_jbl4 = new JLabel("申请密码保护", JLabel.CENTER);
        jp2_jb1 = new JButton(new ImageIcon(ClassLoader.getSystemResource("image/clear.gif")));
        jp2_jb1.addActionListener(this);
        jp2_jtf = new JTextField();
        jp2_jpf = new JPasswordField();
        jp2_jcb1 = new JCheckBox("隐身登录");
        jp2_jcb2 = new JCheckBox("记住密码");

        //QQ登录窗口
        //把控件按照顺序加入到jp2
        jp2.add(jp2_jbl1);
        jp2.add(jp2_jtf);
        jp2.add(jp2_jb1);
        jp2.add(jp2_jbl2);
        jp2.add(jp2_jpf);
        jp2.add(jp2_jbl3);
        jp2.add(jp2_jcb1);
        jp2.add(jp2_jcb2);
        jp2.add(jp2_jbl4);

        //创建选项卡窗口
        jtp = new JTabbedPane();
        jtp.add("账号登录", jp2);
        jp3 = new JPanel();
        //jtp.add("手机号码", jp3);
        jp4 = new JPanel();
        //jtp.add("电子邮件", jp4);

        //处理南部
        jp1 = new JPanel();
        jp1_jb1 = new JButton(new ImageIcon(ClassLoader.getSystemResource("image/denglu.gif")));
        //响应用户点击登录
        jp1_jb1.addActionListener(this);
        jp1_jb2 = new JButton(new ImageIcon(ClassLoader.getSystemResource("image/quxiao.gif")));

        jp1_jb3 = new JButton(new ImageIcon(ClassLoader.getSystemResource("image/xiangdao.gif")));

        //把三个按钮放入到jp1
        jp1.add(jp1_jb1);
        jp1.add(jp1_jb2);
        jp1.add(jp1_jb3);

        this.add(jbl1, "North");
        this.add(jtp, "Center");
        //..把jp1放在南部
        this.add(jp1, "South");
        this.setTitle("互联世界");
        this.setSize(350, 240);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jp2_jb1) {
            //清楚账号密码
            jp2_jtf.setText("");
            jp2_jpf.setText("");
        }
        if (e.getSource() == jp1_jb1) {
            //登录  校验账号
            if (StringUtils.isEmpty(jp2_jtf.getText())) {
                JOptionPane.showMessageDialog(this, "请输入账号");
            } else {
                User user = new User();
                user.setUserName(jp2_jtf.getText());
                JOptionPane.showMessageDialog(this, "登录成功，你好:" + user.getUserName());
                Message message = new Message();
                message.setExtType(1);
                message.setExtType(1);
                ws.send(JSON.toJSONString(message));
                this.dispose();
                new HomePage(user);
            }
        }
    }
}
