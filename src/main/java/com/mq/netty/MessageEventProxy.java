package com.mq.netty;

/**
 * 消息处理代理接口（消息处理前，处理后，执行相关操作）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 下午5:25:20
 */
public interface MessageEventProxy<T> {
    /**
     * handleMessage之前调用
     * 
     * @param msg
     * @date: 2019年9月20日 下午5:42:56
     */
    void beforeMessage(T msg);

    /**
     * handleMessage之后调用
     * 
     * @param msg
     * @date: 2019年9月20日 下午5:43:14
     */
    void afterMessage(T msg);
}
