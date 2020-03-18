package e.word.net.model;

import lombok.Data;

@Data
public class Message {
    int mainType;
    int extType;
    Event event;
}
