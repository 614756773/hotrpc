package cn.hotpot.rpc.common.netty.server;

import cn.hotpot.rpc.common.netty.codec.Decoder;
import cn.hotpot.rpc.common.netty.codec.Encoder;
import cn.hotpot.rpc.common.netty.model.Request;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qinzhu
 * @since 2019/12/12
 */
@Slf4j
public abstract class Server {

    private static Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private AtomicBoolean serverStatus = new AtomicBoolean(false);

    static Object getService(String className) {
        return serviceMap.get(className);
    }

    public void start(Integer port) {
        checkServerStatus();
        registerService(serviceMap);
        listen(port);
    }

    /**
     * 检查服务状态
     */
    private void checkServerStatus() {
        if (!serverStatus.get()) {
            synchronized (Server.class) {
                if (serverStatus.get()) {
                    throw new RuntimeException("服务已经启动");
                }
            }
        }
        serverStatus.set(true);
    }

    /**
     * 监听请求
     */
    private void listen(Integer port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new Decoder(Request.class),
                                    new Encoder(),
                                    new ServerHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = b.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 把rpc服务类扫描出来实例化，并缓存起来
     */
    abstract void registerService(Map<String, Object> serviceMap);
}
