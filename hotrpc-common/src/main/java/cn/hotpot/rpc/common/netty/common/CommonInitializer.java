package cn.hotpot.rpc.common.netty.common;

import cn.hotpot.rpc.common.netty.codec.Decoder;
import cn.hotpot.rpc.common.netty.codec.Encoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


/**
 * @author qinzhu
 * @since 2019/12/10
 *
 * 通用handler配置，它们使用相同的编解码器
 */
public class CommonInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * 根据server端和client端传入不同的handler
     */
    private ChannelHandler channelHandler;

    public CommonInitializer(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        socketChannel.pipeline().addLast(
                new LengthFieldBasedFrameDecoder(65536,
                        0,
                        4,
                        4,
                        0),
                new Decoder(),
                new Encoder(),
                this.channelHandler);
    }
}
