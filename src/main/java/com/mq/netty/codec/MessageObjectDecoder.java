package com.mq.netty.codec;

import java.util.List;

import com.mq.serialize.IMessageCodecUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageObjectDecoder extends ByteToMessageDecoder {

    /**
     * 消息长度信息
     */
    private static final int MESSAGE_LEN_INFO = IMessageCodecUtil.MESSAGE_LENGTH;
    IMessageCodecUtil codecUtil;

    public MessageObjectDecoder(IMessageCodecUtil codecUtil) {
        super();
        this.codecUtil = codecUtil;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 先读取消息长度信息，再根据消息长度，向后读取消息

        // 可读的消息不到长度，等待下次读取
        if (in.readableBytes() < MESSAGE_LEN_INFO) {
            return;
        }

        // 记录当前读标记
        in.markReaderIndex();
        int msgLen = in.readInt();
        // 如果消息还没有传完，先不读取，重置读标记
        if (in.readableBytes() < msgLen) {
            in.resetReaderIndex();
            return;
        }

        // 如果可读取消息已经到了
        byte[] data = new byte[msgLen];
        in.readBytes(data);
        out.add(codecUtil.decode(data));
    }

}
