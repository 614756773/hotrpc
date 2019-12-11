package cn.hotpot.rpc.common.service.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qinzhu
 * @since 2019/12/11
 */
@Data
@Accessors(chain = true)
public class User {
    private String userCode;
    private String userName;
    private String sex;
}
