package cn.hotpot.rpc.common.netty.server;

import cn.hotpot.rpc.common.config.SpringApplicationContextHelper;
import cn.hotpot.rpc.common.netty.model.Request;
import cn.hotpot.rpc.common.netty.model.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author qinzhu
 * @since 2019/12/12
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("服务端收到客户端的请求{}", msg.toString());
        Request request = (Request) msg;
        String className = request.getClassName();
        Object result = null;
        try {
            Class<?> clazz = Class.forName(className);
            Object bean = Server.getService(className);
            Method method = clazz.getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(bean, request.getParams());
        } catch (Exception e) {
            Response response = new Response().setId(request.getId()).setThrowable(e);
            ctx.writeAndFlush(response);
            return;
        }

        Response response = new Response().setId(request.getId()).setResult(result);
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("建立连接");
        super.channelActive(ctx);
    }
}
