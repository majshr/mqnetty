package com.mq.consumer.hook;

import com.mq.model.message.ConsumerAckMessage;
import com.mq.model.message.Message;

/**
 * 消费者收到消息后，进行处理的hook，由消费者自己定义，如何处理消息
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月19日 下午5:09:11
 */
public interface ConsumeProducerMessageHook {
    /**
     * 生成消费应答消息hook方法
     * 
     * @param paramMessage
     * @return ConsumerAckMessage
     * @date: 2019年9月19日 下午5:10:39
     */
    ConsumerAckMessage hookMessage(Message paramMessage);
}
