package cn.hotpot.rpc.common.netty.client;

import cn.hotpot.rpc.common.netty.common.CommonInitializer;
import cn.hotpot.rpc.common.netty.model.Request;
import cn.hotpot.rpc.common.netty.model.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
public class Client {
    private Channel channel;
    private BlockingQueue<Response> queue = new ArrayBlockingQueue<>(1);

    public Client(String host, Integer port) {
        connect(host, port);
    }

    public Response send(Request request) {
        channel.writeAndFlush(request);
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void connect(String host, Integer port) {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new CommonInitializer(new ClientHandler(queue)));
            Channel channel = b.connect(host, port).sync().channel();
            this.channel = channel;
            this.channel.closeFuture().sync();// TODO 测试一下是否不需要关闭
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
