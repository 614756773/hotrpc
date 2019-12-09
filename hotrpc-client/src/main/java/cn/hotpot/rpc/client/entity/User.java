package cn.hotpot.rpc.client.entity;

import lombok.Data;

/**
 * @author qinzhu
 * @since 2019/12/9
 */
@Data
public class User {
    private String userCode;

    private String userName;

    private String password;
}
