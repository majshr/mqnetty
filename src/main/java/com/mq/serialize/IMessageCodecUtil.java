package com.mq.serialize;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * 消息编解码工具接口
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 上午10:30:25
 */
public interface IMessageCodecUtil {
    /**
     * 消息长度信息，默认按int类型，4个字节
     */
    final public static int MESSAGE_LENGTH = 4;

    /**
     * 编码对象到buf
     * 
     * @param out
     * @param message
     * @throws IOException
     * @date: 2019年9月20日 上午10:34:22
     */
    public void encode(final ByteBuf out, final Object message) throws IOException;

    /**
     * 解码字节数组为对象
     * 
     * @param body
     * @return
     * @throws IOException
     *             Object
     * @date: 2019年9月20日 上午10:34:38
     */
    public Object decode(byte[] body) throws IOException;
}
