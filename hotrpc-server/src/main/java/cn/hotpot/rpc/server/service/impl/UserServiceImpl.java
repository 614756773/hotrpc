package cn.hotpot.rpc.server.service.impl;

import cn.hotpot.rpc.common.annotation.RpcReplier;
import cn.hotpot.rpc.common.service.UserService;
import cn.hotpot.rpc.common.service.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author qinzhu
 * @since 2019/12/11
 */
@RpcReplier
public class UserServiceImpl implements UserService {
    @Override
    public void save(User user) {
        System.out.println("已存储");
    }

    @Override
    public List<User> list() {
        return Collections.singletonList(new User().setSex("boy")
                .setUserCode(UUID.randomUUID().toString())
                .setUserName("Tom"));
    }

    @Override
    public String getOne() {
        return "TODO";
    }
}
