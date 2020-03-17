package e.word.net.db;

import e.word.net.model.Room;
import e.word.net.model.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Memory {
    // TODO: 2020/3/16 用户表
    public static List<User> users = new ArrayList<>();
    // TODO: 2020/3/16 房间号
    public static List<Room> rooms = new ArrayList<>();
}
