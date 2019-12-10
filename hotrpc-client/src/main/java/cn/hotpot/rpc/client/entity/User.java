package cn.hotpot.rpc.client.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qinzhu
 * @since 2019/12/9
 */
@Data
@Accessors(chain = true)
public class User {
    private String userCode;

    private String userName;

    private String password;
}
