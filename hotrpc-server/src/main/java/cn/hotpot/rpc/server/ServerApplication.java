package cn.hotpot.rpc.server;

import cn.hotpot.rpc.common.netty.server.Server;

/**
 * @author qinzhu
 * @since 2019/12/11
 */
public class ServerApplication {
    public static void main(String[] args) {
        Server.start(8864);
    }
}
