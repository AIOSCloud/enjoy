package e.wrod.net.common;

import e.wrod.net.component.JCard;
import e.wrod.net.view.AIPage;
import org.apache.log4j.Logger;

import java.awt.*;

/**
 * 客户端线程,1.监听用户行为 2.监听其他的客户的行为
 */
public class UClient extends Thread {
    Logger logger = Logger.getLogger(UClient.class);
    AIPage main;
    int position;
    int i = 10;
    boolean isRun = true;

    public void setIsRun(boolean isRun) {
        this.isRun = isRun;
    }

    public UClient(AIPage main, int position) {
        this.main = main;
        this.position = position;
    }

    @Override
    public void run() {
        logger.info("im user " + position);
        while (i > -1 && isRun) {
            logger.debug("isRun: " + isRun);
            main.getTime()[position].setVisible(true);
            main.getTime()[position].setText("倒计时:" + i--);
            second(1);// 等一秒
        }
        if (i == -1)// 正常终结，说明超时
            main.getTime()[1].setText("不抢");
        main.getLandlord()[0].setVisible(false);
        main.getLandlord()[1].setVisible(false);
        //设置牌为可点击
        for (JCard card2 : main.getUsers().get(position).getPlayers()) {
            card2.setCanClick(true);// 可被点击
        }
        // TODO: 2020/3/15 通过消息判断地主
    }

    // 设定地主
    public void setLord(int i) {
        Point point = new Point();
        if (i == 1)// 我是地主
        {
            point.x = 80;
            point.y = 430;
            main.getUsers().get(1).setLordFlag(true);// 设定地主
        }
        main.getLord().setLocation(point);
        main.getLord().setVisible(true);
    }

    // 地主牌翻看
    public void openlord(boolean is) {
        for (int i = 0; i < 3; i++) {
            if (is)
                main.getLordList().get(i).turnFront(); // 地主牌翻看
            else {
                main.getLordList().get(i).turnRear(); // 地主牌闭合
            }
            main.getLordList().get(i).setCanClick(true);
        }
    }

    // 等待i秒
    public void second(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
