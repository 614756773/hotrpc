package cn.hotpot.rpc.common.netty.client;

import cn.hotpot.rpc.common.netty.model.Response;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
@Slf4j
public class ClientHandler extends ChannelDuplexHandler {
    private BlockingQueue<Response> queue;

    public ClientHandler(BlockingQueue<Response> queue) {
        this.queue = queue;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log(msg);
        if (!(msg instanceof Response)) {
            ctx.close();
            return;
        }
        Response response = (Response) msg;
        queue.add(response);
    }

    private void log(Object msg) {
        try {
            log.debug("客户端收到返回的消息，{}", JSONObject.toJSON(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
