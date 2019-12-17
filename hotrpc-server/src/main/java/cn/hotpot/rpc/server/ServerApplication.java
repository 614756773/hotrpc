package cn.hotpot.rpc.server;

import cn.hotpot.rpc.common.netty.server.AdaptSpringServer;

/**
 * @author qinzhu
 * @since 2019/12/11
 */
public class ServerApplication {
    public static void main(String[] args) {
        new AdaptSpringServer().start(8864);
    }
}
