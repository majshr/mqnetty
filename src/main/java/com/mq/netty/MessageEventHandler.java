package com.mq.netty;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息处理handler方法接口
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 下午5:24:39
 */
public interface MessageEventHandler<T> {
    /**
     * 处理消息
     * 
     * @param ctx
     * @param msg
     * @date: 2019年9月20日 下午5:42:32
     */
    void handleMessage(ChannelHandlerContext ctx, T msg);
}
