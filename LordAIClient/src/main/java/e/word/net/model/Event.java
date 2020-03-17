package e.word.net.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Event {
    boolean next;
    // TODO: 2020/3/17 用户牌 
    List<Card> players = new ArrayList<>();
    // TODO: 2020/3/17 用户出牌 
    List<Card> shows = new ArrayList<>();
}
