package e.wrod.net.model;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class User {
    //用户Id
    private int userId;
    //用户名称
    private String userName;
    //用户密码
    private String password;
}
