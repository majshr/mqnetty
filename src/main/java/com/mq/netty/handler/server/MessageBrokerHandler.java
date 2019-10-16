package com.mq.netty.handler.server;

import java.util.concurrent.atomic.AtomicReference;

import com.mq.broker.hook.ConsumerMessageListener;
import com.mq.broker.hook.ProducerMessageListener;
import com.mq.broker.strategy.BrokerStrategyContext;
import com.mq.model.message.msgenum.MessageSource;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;
import com.mq.netty.handler.ShareMessageEventWrapper;

import io.netty.channel.ChannelHandlerContext;

/**
 * broker消息处理handler
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午4:55:11
 */
public class MessageBrokerHandler extends ShareMessageEventWrapper<RequestMessage> {
    private AtomicReference<ProducerMessageListener> hookProducer;
    private AtomicReference<ConsumerMessageListener> hookConsumer;

    /** 消息对象 */
    private AtomicReference<RequestMessage> message = new AtomicReference<RequestMessage>();

    /**
     * 构建生产者hook
     * 
     * @param hookProducer
     * @return MessageBrokerHandler
     * @date: 2019年9月23日 下午5:04:29
     */
    public MessageBrokerHandler withProducerHook(ProducerMessageListener hookProducer) {
        this.hookProducer = new AtomicReference<ProducerMessageListener>(hookProducer);
        return this;
    }

    /**
     * 构建消费者hook
     * 
     * @param hookConsumer
     * @return MessageBrokerHandler
     * @date: 2019年9月23日 下午5:04:45
     */
    public MessageBrokerHandler withConsumerHook(ConsumerMessageListener hookConsumer) {
        this.hookConsumer = new AtomicReference<ConsumerMessageListener>(hookConsumer);
        return this;
    }

    @Override
    public void beforeMessage(RequestMessage msg) {
        message.set(msg);
    }

    @Override
    public void handleMessage(ChannelHandlerContext ctx, RequestMessage msg) {
        RequestMessage request = message.get();

        // broker回应消息
        ResponseMessage response = new ResponseMessage();
        // 消息ID，消息来源
        response.setMsgId(request.getMsgId());
        response.setMsgSource(MessageSource.MQBroker);

        // 响应策略
        BrokerStrategyContext strategy = new BrokerStrategyContext(request, response, ctx);
        strategy.setHookConsumer(hookConsumer.get());
        strategy.setHookProducer(hookProducer.get());
        strategy.invoke();
    }
}
