package com.mq.broker.strategy;

import com.mq.broker.hook.ConsumerMessageListener;
import com.mq.broker.hook.ProducerMessageListener;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * broker策略
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午5:45:12
 */
public interface BrokerStrategy {
    /**
     * 消息分发
     * 
     * @param request
     * @param response
     * @date: 2019年9月23日 下午5:46:07
     */
    void messageDispatch(RequestMessage request, ResponseMessage response);

    /**
     * 生产者hook
     * 
     * @param hookProducer
     * @date: 2019年9月23日 下午5:46:17
     */
    void setHookProducer(ProducerMessageListener hookProducer);

    /**
     * 消费者hook
     * 
     * @param hookConsumer
     * @date: 2019年9月23日 下午5:46:36
     */
    void setHookConsumer(ConsumerMessageListener hookConsumer);

    /**
     * 设置处理器
     * 
     * @param channelHandler
     * @date: 2019年9月23日 下午5:48:33
     */
    void setCtx(ChannelHandlerContext channelHandler);
}
