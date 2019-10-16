package com.mq.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.io.Closer;

/**
 * kryo序列化，反序列化工具类
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 上午10:40:34
 */
public class KryoSerializeUtil {
    private static KryoPool kryoPool = KryoPoolFactory.getKryoPool();

    private static Closer closer = Closer.create();

    /**
     * 序列化对象到流
     * 
     * @param output
     * @param object
     * @throws IOException
     *             void
     * @date: 2019年9月20日 上午10:47:07
     */
    public static void serialize(OutputStream output, Object object) throws IOException {
        try {
            Kryo kryo = kryoPool.borrow();
            Output out = new Output(output);
            closer.register(out);
            closer.register(output);
            kryo.writeClassAndObject(out, object);
            kryoPool.release(kryo);
        } finally {
            closer.close();
        }
    }

    /**
     * 从流中反序列化为对象
     * 
     * @param input
     * @return
     * @throws IOException
     *             Object
     * @date: 2019年9月20日 上午10:51:01
     */
    public static Object deserialize(InputStream input) throws IOException {
        try {
            Kryo kryo = kryoPool.borrow();
            Input in = new Input(input);
            closer.register(in);
            closer.register(input);
            Object result = kryo.readClassAndObject(in);
            kryoPool.release(kryo);
            return result;
        } finally {
            closer.close();
        }
    }
}
