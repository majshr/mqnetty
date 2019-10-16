package com.mq.broker.strategy;

import com.mq.broker.hook.ConsumerMessageListener;
import com.mq.broker.hook.ProducerMessageListener;
import com.mq.model.message.Message;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * 生产者消息，消息处理策略
 * 
 * @author mengaijun
 * @date: 2019年9月23日 下午5:54:54
 */
public class BrokerProducerMessageStrategy implements BrokerStrategy {

    private ProducerMessageListener hookProducer;
    private ChannelHandlerContext channelHandler;

    @Override
    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        Message message = (Message) request.getMsgParams();
        hookProducer.hookProducerMessage(message, request.getMsgId(), channelHandler.channel());
    }

    @Override
    public void setHookProducer(ProducerMessageListener hookProducer) {
        this.hookProducer = hookProducer;
    }

    @Override
    public void setHookConsumer(ConsumerMessageListener hookConsumer) {

    }

    @Override
    public void setCtx(ChannelHandlerContext channelHandler) {
        this.channelHandler = channelHandler;
    }

}
