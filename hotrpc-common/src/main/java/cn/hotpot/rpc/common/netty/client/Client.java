package cn.hotpot.rpc.common.netty.client;

import cn.hotpot.rpc.common.netty.common.CommonInitializer;
import cn.hotpot.rpc.common.netty.common.Constants;
import cn.hotpot.rpc.common.netty.model.Request;
import cn.hotpot.rpc.common.netty.model.Response;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
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
                    .handler(new CommonInitializer(new ClientHandler(queue), Response.class));
            log.debug("客户端开始连接");
            Channel channel = b.connect(host, port).sync().channel();
            this.channel = channel;
            log.debug("连接完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
