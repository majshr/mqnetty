package com.mq.netty.codec;

import com.mq.serialize.IMessageCodecUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 消息对象编码
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 上午11:25:34
 */
public class MessageObjectEncoder extends MessageToByteEncoder<Object> {

    private IMessageCodecUtil util = null;

    public MessageObjectEncoder(final IMessageCodecUtil util) {
        this.util = util;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        util.encode(out, msg);
    }

}
