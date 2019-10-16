package com.mq.broker.hook;

import com.mq.model.message.Message;

import io.netty.channel.Channel;

/**
 * 生产者消息监听接口
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午4:58:31
 */
public interface ProducerMessageListener {
    /**
     * 处理生产者消息hook
     * 
     * @param msg
     * @param requestId
     * @param channel
     * @date: 2019年10月15日 上午10:06:31
     */
    void hookProducerMessage(Message msg, String requestId, Channel channel);
}
