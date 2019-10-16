package com.mq.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * kryo编解码工具
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 上午11:04:11
 */
public class KryoCodecUtil implements IMessageCodecUtil {

    @Override
    public void encode(ByteBuf out, Object message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            KryoSerializeUtil.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } finally {
            byteArrayOutputStream.close();
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(body);
            Object obj = KryoSerializeUtil.deserialize(byteArrayInputStream);
            return obj;
        } finally {
            byteArrayInputStream.close();
        }
    }

    /** 单例获取实例 */
    private static KryoCodecUtil kryoCodecUtil = new KryoCodecUtil();

    private KryoCodecUtil() {

    }

    public static KryoCodecUtil getInstance() {
        return kryoCodecUtil;
    }

}
