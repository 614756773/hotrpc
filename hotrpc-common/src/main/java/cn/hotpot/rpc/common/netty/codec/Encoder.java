package cn.hotpot.rpc.common.netty.codec;

import cn.hotpot.rpc.common.netty.common.Constants;
import cn.hotpot.rpc.common.netty.model.Request;
import cn.hotpot.rpc.common.netty.model.Response;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author qinzhu
 * @since 2019/12/10
 */
@Slf4j
public class Encoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf byteBuf) throws Exception {
        String strMsg = JSONObject.toJSONString(message);
        log.debug("开始编码：{}", strMsg);
        byte[] bytes = strMsg.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(Constants.MAGINC_NUMBER);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        log.debug("编码完毕");
    }
}
