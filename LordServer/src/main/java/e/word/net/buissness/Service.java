package e.word.net.buissness;

import com.alibaba.fastjson.JSON;
import e.word.net.comon.Common;
import e.word.net.model.Card;
import e.word.net.model.Event;
import e.word.net.model.Room;
import e.word.net.model.User;
import e.word.net.server.ChannelSupervise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Service {
    private static Logger logger = Logger.getLogger(Service.class);
    // TODO: 2020/3/17 robot
    private static User robot;
    // TODO: 2020/3/17 服务登录时，保存所有的用户信息
    public static ConcurrentMap<String, User> userMap = new ConcurrentHashMap<String, User>();
    public static ConcurrentMap<String, Room> house = new ConcurrentHashMap<String, Room>();

    /**
     * 登录验证
     *
     * @return
     */
    public static String login(String message) {
        logger.debug("服务端校验用户登录数据......");
        User user = JSON.parseObject(message, User.class);
        return JSON.toJSONString(user);
    }

    /**
     * 创建链接,建立websocket
     *
     * @return
     */
    public static void link(Event event, String channelId) {
        User user = event.getUser();
        user.setUserId(channelId);
        if (user.isRobot()) {
            robot = user;
        } else {
            userMap.put(user.getUserId(), user);
        }
        Event result = new Event();
        result.setUser(user);
        result.setType("建立链接");
        TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(result));
        ChannelSupervise.findChannel(user.getUserId()).writeAndFlush(tws);
    }

    /**
     * @param event
     */
    public static void createRoom(Event event) {
        if (event.getType().equals("创建房间")) {
            logger.debug("创建房间.....");
            User user = event.getUser();
            logger.debug("房间创建user。。。。。。" + user);
            //创建房间
            Room room = new Room();
            if (!event.isOnline()) {
                // TODO: 2020/3/18 单机斗地主
                room.getUsers().add(0, robot);
                room.getUsers().add(1, userMap.get(user.getUserId()));
                room.getUsers().add(2, robot);
            } else {
                // TODO: 2020/3/18 在线斗地主
            }
            room.washCard();
            room.dealCard();
            room.orders();
            Event result;
            // TODO: 2020/3/17 推送各自的牌面到用户端
            for (int i = 0; i < room.getUsers().size(); i++) {
                logger.debug("id" + room.getUsers().get(i).getUserId());
                result = new Event();
                result.setType("发牌");
                result.setLordList(room.getLordCards());
                result.setPlayers(room.getPlayerList()[i]);
                user = room.getUsers().get(i);
                user.setIndex(i);
                result.setUser(user);
                result.setUsers(room.getUsers());
                result.setIndex(i);
                house.put(user.getUserId(), room);
                if (!user.isRobot()) {
                    TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(result));
                    ChannelSupervise.findChannel(user.getUserId()).writeAndFlush(tws);
                }
            }
        }
    }

    /**
     * 抢地主判定
     */
    public static void landLord(Event event) {
        logger.debug("开始抢地主......");
        // TODO: 2020/3/17 获取房间信息
        User user = event.getUser();
        Room room = house.get(user.getUserId());
        int lordIndex;
        List<User> users = room.getUsers();
        if (!event.isOnline()) {
            Event result = new Event();
            result.setType("地主");
            result.setLordList(room.getLordCards());
            // TODO: 2020/3/18 单机斗地主
            if (event.getIndex() == 1) {
                // TODO: 2020/3/18 如果用户抢地主 设置用户为地主
                lordIndex = 1;
                room.setLordIndex(1);
                room.setTurn(1);
                // TODO: 2020/3/18 设置返回客户端信息
                result.setLordIndex(1);
                result.setTurn(1);
                // TODO: 2020/3/18 地主牌
                room.getPlayerList()[1].addAll(room.getLordCards());
                Common.order(room.getPlayerList()[1]);
            } else {
                // TODO: 2020/3/18 用户不抢地主 地主判定
                if (Common.getScore(room.getPlayerList()[0]) > Common.getScore(room.getPlayerList()[2])) {
                    // TODO: 2020/3/18  机器人1的牌面比机器人2的牌面好
                    lordIndex = 0;
                    room.setLordIndex(0);
                    room.setTurn(0);
                    //返回客户端信息
                    result.setLordIndex(0);
                    result.setTurn(0);
                    //房间信息跟新
                    room.getPlayerList()[0].addAll(room.getLordCards());
                    Common.order(room.getPlayerList()[0]);
                } else {
                    //房间信息跟新
                    lordIndex = 2;
                    room.setLordIndex(2);
                    room.setTurn(2);
                    room.getPlayerList()[1].addAll(room.getLordCards());
                    //返回客户端信息
                    result.setLordIndex(2);
                    result.setTurn(2);
                }
            }
            for (int i = 0; i < users.size(); i++) {
                result.setIndex(i);
                result.setUser(users.get(i));
                result.setPlayers(room.getPlayerList()[i]);
                if (!(users.get(i).isRobot() && i != lordIndex)) {
                    // TODO: 2020/3/20  非地主用户不需要发信息
                    TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(result));
                    ChannelSupervise.findChannel(users.get(i).getUserId()).writeAndFlush(tws);
                }
            }
        }
    }

    /**
     * 玩牌
     */
    public static void play(Event event) {
        logger.debug("开始出牌游戏......" + house.size());
        for (String key : house.keySet()) {
            logger.debug("房间信息" + key);
        }
        //获取用户信息，获取用户位置
        User user = event.getUser();
        //获取出的牌
        List<Card> shows = event.getShows();
        int showIndex = event.getShowIndex();
        int index = event.getIndex();
        // TODO: 2020/3/18 获取房间信息
        Room room = house.get(user.getUserId());
        boolean play = event.isPlay();
        int playIndex = event.getPlayIndex();
        List<User> users = room.getUsers();
        int turn = (index + 1) % 3;
        // TODO: 2020/3/18 更新房间信息
        // 设置下级的未知
        room.setTurn(turn);
        // 判断自己是否出牌
        if (showIndex == index) {
            //更新房间信息
            room.getPlayerList()[index].removeAll(shows);
            //重新排序
            Common.order(room.getPlayerList()[index]);
            // 保存玩家本次出牌
            room.getShowsList()[index].clear();
            room.getShowsList()[index].addAll(shows);
        }
        // TODO: 2020/3/19 如果自己出牌
        // TODO: 2020/3/18 组装消息发送到下家
        Event result = new Event();
        result.setType("出牌");
        // 发送下一个玩家的牌
        result.setShows(shows);
        result.setShowIndex(showIndex);
        result.setTurn(turn);
        result.setLordIndex(room.getLordIndex());
        result.setPlay(play);
        result.setPlayIndex(playIndex);
        for (int i = 0; i < users.size(); i++) {
            result.setIndex(i);
            result.setUser(users.get(i));
            result.setPlayers(room.getPlayerList()[i]);
            if (!(users.get(i).isRobot() && turn != i)
                    && i != playIndex) {
                // TODO: 2020/3/18 发送上家出的牌给下家
                TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(result));
                ChannelSupervise.findChannel(users.get(i).getUserId()).writeAndFlush(tws);
            }
        }
    }
}
