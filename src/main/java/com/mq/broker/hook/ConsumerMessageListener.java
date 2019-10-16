package com.mq.broker.hook;

import com.mq.model.RemoteChannelData;
import com.mq.model.message.SubscribeMessage;

/**
 * 消费者消息监听接口
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午4:59:39
 */
public interface ConsumerMessageListener {
    /**
     * 订阅消息hook方法
     * 
     * @param msg
     * @param channelData
     *            void
     * @date: 2019年9月24日 下午3:22:41
     */
    void hookConsumerMessage(SubscribeMessage msg, RemoteChannelData channelData);
}
