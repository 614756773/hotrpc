package cn.hotpot.rpc.common.netty.codec;

import cn.hotpot.rpc.common.netty.common.Constants;
import cn.hotpot.rpc.common.netty.model.Request;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 8) {
            return;
        }

        byteBuf.markReaderIndex();

        // 如果魔数不对则直接关闭连接
        int magicNumber = byteBuf.readInt();
        if (magicNumber != Constants.MAGINC_NUMBER) {
            channelHandlerContext.close();
            return;
        }

        // 如果可读取数据不足，则重置read指针，暂时不添加到返回结果中
        int dateLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dateLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[dateLength];
        byteBuf.readBytes(bytes);
        Request request = JSONObject.parseObject(new String(bytes, StandardCharsets.UTF_8), Request.class);
        list.add(request);
    }
}
