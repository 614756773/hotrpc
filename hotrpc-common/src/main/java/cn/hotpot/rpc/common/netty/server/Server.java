package cn.hotpot.rpc.common.netty.server;

import cn.hotpot.rpc.common.annotation.RpcReplier;
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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qinzhu
 * @since 2019/12/12
 */
@Slf4j
public class Server {

    private static Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private static AtomicBoolean serverStatus = new AtomicBoolean(false);

    public static void start(Integer port) {
        checkServerStatus();
        registerService();// TODO 这儿有bug，没有把service放入map中
        listen(port);
    }

    /**
     * 检查服务状态
     */
    private static void checkServerStatus() {
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
    private static void listen(Integer port) {
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
     * 把rpc服务类实例化，并缓存起来
     */
    private static void registerService() {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false);
        componentProvider.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            log.debug(metadataReader.getClassMetadata().getClassName());
            return metadataReader.getAnnotationMetadata().getAnnotationTypes().contains(RpcReplier.class.getName());
        });

        Set<BeanDefinition> beanDefinitions = componentProvider.findCandidateComponents("cn.hotpot");
        beanDefinitions.forEach(beanDefinition -> {
            String className = beanDefinition.getBeanClassName();
            try {
                Class<?> aClass = Class.forName(className);
                Object service = aClass.newInstance();
                Server.serviceMap.put(aClass.getInterfaces()[0].getName(), service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    static Object getService(String className) {
        return Server.serviceMap.get(className);
    }
}
