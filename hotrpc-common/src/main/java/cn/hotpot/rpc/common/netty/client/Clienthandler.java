package cn.hotpot.rpc.common.netty.client;

        import cn.hotpot.rpc.common.netty.model.Request;
        import cn.hotpot.rpc.common.netty.model.Response;
        import io.netty.channel.ChannelDuplexHandler;
        import io.netty.channel.ChannelHandlerContext;
        import io.netty.channel.ChannelPromise;

        import java.lang.reflect.InvocationTargetException;
        import java.lang.reflect.Method;
        import java.util.concurrent.BlockingQueue;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
public class Clienthandler extends ChannelDuplexHandler {
    private BlockingQueue<Response> queue;

    public Clienthandler(BlockingQueue<Response> queue) {
        this.queue = queue;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Request)) {
            ctx.close();
            return;
        }
        Request request = (Request) msg;
        Class aClass = Class.forName(request.getClassName());
        Object o = aClass.newInstance();
        Method method = aClass.getMethod(request.getMethodName(), request.getParamTypes());
        Response response = null;
        try {
            Object result = method.invoke(o, request.getParams());
            response = produceResponse(result, request.getId());
        } catch (Exception e) {
            response = produceResponseError(e, request.getId());
        } finally {
            queue.add(response);
        }
    }

    private Response produceResponseError(Throwable throwable, String id) {
        return new Response()
                .setId(id)
                .setThrowable(throwable);
    }

    private Response produceResponse(Object result, String id) {
        return new Response()
                .setId(id)
                .setResult(result);
    }
}
