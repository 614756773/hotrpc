package cn.hotpot.rpc.client.service;

import cn.hotpot.rpc.client.entity.User;
import cn.hotpot.rpc.common.annotation.RpcCaller;

import java.util.List;

/**
 * @author qinzhu
 * @since 2019/12/9
 */
@RpcCaller
public interface UserService {
    void save(User user);

    List<User> list();
}
