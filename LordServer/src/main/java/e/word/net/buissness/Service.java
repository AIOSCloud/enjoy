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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Service {
    private static Logger logger = Logger.getLogger(Service.class);
    // TODO: 2020/3/17 robot
    private static User robot;
    // TODO: 2020/3/17 服务登录时，保存所有的用户信息
    private static ConcurrentMap<String, User> userMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Room> house = new ConcurrentHashMap<>();

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
     * 创建链接
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
        // TODO: 2020/3/17 获取用户信息
        User user = event.getUser();
        // TODO: 2020/3/17 获取房间信息
        Room room = house.get(user.getUserId());
        List<User> users = room.getUsers();
        if (!event.isOnline()) {
            Event result = new Event();
            result.setType("地主");
            result.setLordList(room.getLordCards());
            // TODO: 2020/3/18 单机斗地主
            if (user.getUserId().equals(users.get(1).getUserId()) && !users.get(1).isRobot() && event.isLord()) {
                // TODO: 2020/3/18 如果用户抢地主 设置用户为地主
                room.setLordIndex(1);
                room.setTurn(1);
                result.setLordIndex(1);
                result.setTurn(1);
                // TODO: 2020/3/18 地主牌
                room.getPlayerList()[1].addAll(room.getLordCards());
            } else {
                // TODO: 2020/3/18 用户不抢地主 地主判定
                if (Common.getScore(room.getPlayerList()[0]) > Common.getScore(room.getPlayerList()[2])) {
                    // TODO: 2020/3/18  机器人1的牌面比机器人2的牌面好
                    room.setLordIndex(0);
                    room.setTurn(0);
                    result.setLordIndex(0);
                    result.setTurn(1);
                    room.getPlayerList()[0].addAll(room.getLordCards());
                } else {
                    room.setLordIndex(2);
                    room.setTurn(2);
                    room.getPlayerList()[1].addAll(room.getLordCards());
                }
            }
            for (User u : users) {
                TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(result));
                ChannelSupervise.findChannel(u.getUserId()).writeAndFlush(tws);
            }
        }
    }

    /**
     * 玩牌
     */
    public static void play(Event event) {
        //获取用户信息，获取用户位置
        User user = new User();
        //获取出的牌
        List<Card> shows = event.getShows();
        // TODO: 2020/3/18 获取房间信息
        Room room = house.get(user.getUserId());
        // TODO: 2020/3/18 获取下家用户信息
        User next = room.getUsers().get((user.getIndex() + 1) % 3);
        //移除玩家出的牌
        room.getPlayerList()[user.getIndex()].removeAll(shows);
        Common.order(room.getPlayerList()[user.getIndex()]);
        room.getShowsList()[user.getIndex()].clear();
        room.getShowsList()[user.getIndex()].addAll(shows);

        Event result = new Event();
        result.setType("出牌");
        result.setUser(next);
        //设置出牌
        result.setPlayers(room.getPlayerList()[(next.getIndex() + 1) % 3]);
        //上家出牌发送给下家
        result.getShows().addAll(shows);
        // TODO: 2020/3/18 发送上家出的牌给下家
        TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(result));
        ChannelSupervise.findChannel(next.getUserId()).writeAndFlush(tws);
    }
}
