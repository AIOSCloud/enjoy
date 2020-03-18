package e.word.net.model;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class User {
    private String userId;
    private String userName;
    private String password;
    private boolean robot;
}
