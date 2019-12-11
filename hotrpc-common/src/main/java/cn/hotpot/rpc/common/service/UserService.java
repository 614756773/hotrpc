package cn.hotpot.rpc.common.service;

import cn.hotpot.rpc.common.annotation.RpcCaller;
import cn.hotpot.rpc.common.service.entity.User;

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