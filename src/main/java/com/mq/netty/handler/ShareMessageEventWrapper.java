package com.mq.netty.handler;

import com.mq.core.HookMessageEvent;
import com.mq.netty.MessageProcessor;

import io.netty.channel.ChannelHandler.Sharable;

/**
 * sharable的消息包装器
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午4:50:20
 */
@Sharable
public class ShareMessageEventWrapper<T> extends MessageEventWrapper<T, Object> {
    public ShareMessageEventWrapper() {
        super();
    }

    public ShareMessageEventWrapper(MessageProcessor processor) {
        super(processor, null);
    }

    public ShareMessageEventWrapper(MessageProcessor processor, HookMessageEvent<Object> hook) {
        super(processor, hook);
    }
}
