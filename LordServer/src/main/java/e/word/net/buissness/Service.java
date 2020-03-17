package e.word.net.buissness;

import e.word.net.model.Room;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Service {
    // TODO: 2020/3/17 保存房间信息
    private ConcurrentMap<String, Room> roomMap = new ConcurrentHashMap<>();

}
