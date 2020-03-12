package e.wrod.net.model;

import lombok.Data;

@Data
public class Message {
    int mainType;
    int extType;
    User user;
}
