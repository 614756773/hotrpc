package cn.hotpot.rpc.common.netty.client;

import cn.hotpot.rpc.common.netty.codec.Decoder;
import cn.hotpot.rpc.common.netty.codec.Encoder;
import cn.hotpot.rpc.common.netty.model.Request;
import cn.hotpot.rpc.common.netty.model.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
@Slf4j
public class Client {
    private String host;

    private Integer port;

    private Channel channel;

    private BlockingQueue<Response> queue = new ArrayBlockingQueue<>(1);

    public Client(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Response send(Request request) {

        connect(host, port);

        log.debug("客户端开始发送消息");
        channel.writeAndFlush(request);
        log.debug("客户端发送消息完毕");
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
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new Decoder(Response.class),
                                    new Encoder(),
                                    new ClientHandler(queue));
                        }
                    });
            log.debug("客户端开始连接");
            this.channel = b.connect(host, port).sync().channel();
            log.debug("连接完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
