package com.mq.consumer;

import com.mq.consumer.hook.ConsumeProducerMessageHook;
import com.mq.core.HookMessageEvent;
import com.mq.model.message.ConsumerAckMessage;
import com.mq.model.message.Message;
import com.mq.model.message.msgnet.ResponseMessage;

/**
 * 消费者hook MessageEvent，消费消息
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午3:19:05
 */
public class ConsumerHookMessageEvent extends HookMessageEvent<Object> {
    /** 生产者消息hook */
    ConsumeProducerMessageHook hook;

    public ConsumerHookMessageEvent(ConsumeProducerMessageHook hook) {
        this.hook = hook;
    }

    @Override
    public Object callBackMessage(Object message) {
        ResponseMessage response = (ResponseMessage) message;
        if (response.getMsgParams() instanceof Message) {
            // 消费消息，生成消费者ack信息
            ConsumerAckMessage result = hook.hookMessage((Message) response.getMsgParams());
            result.setMsgId(((Message) response.getMsgParams()).getMsgId());
            result.setStatus(ConsumerAckMessage.SUCCESS);
            return result;
        }

        return null;

    }
}
