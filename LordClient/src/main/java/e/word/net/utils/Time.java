package e.word.net.utils;

import com.alibaba.fastjson.JSON;
import e.word.net.model.Event;
import e.word.net.view.RoomPage;

public class Time extends Thread {
    public boolean lord;
    public boolean isRun;
    public RoomPage page;
    public MyWebSocketClient ws;
    public int index;
    public boolean mine;

    public Time(RoomPage page, MyWebSocketClient ws, boolean isRun, boolean lord) {
        this.page = page;
        this.ws = ws;
        this.lord = lord;
        this.isRun = isRun;
    }

    @Override
    public void run() {
        if (lord) {
            int i = 10;
            while (i >= 0 && isRun) {
                page.time[page.mine].setText("倒计时:" + i--);
                page.second(1);
            }
            if (i == -1) {
                page.time[page.mine].setText("不抢");
                //如果是自己 模拟自己抢地主
                page.landlord[0].setVisible(false);
                page.landlord[1].setVisible(false);
                // TODO: 2020/3/17 不抢地主
                Event result = new Event();
                result.setType("抢地主");
                result.setLord(false);
                result.setUser(page.user);
                ws.send(JSON.toJSONString(result));
            }
        } else {
            int i = 30;
            while (i >= 0 && isRun) {
                page.time[page.turn].setText("倒计时:" + i--);
                page.second(1);
            }
            if (i == -1) {
                page.time[page.turn].setText("不要");
                if (mine) {
                    //模拟出牌
                    page.publishCard[0].setVisible(false);
                    page.publishCard[1].setVisible(false);
                    // TODO: 2020/3/18 发送出牌消息
                    Event event = new Event();
                    event.setType("出牌");
                    event.setUser(page.user);
                    ws.send(JSON.toJSONString(event));
                }
            }
        }
    }
}
