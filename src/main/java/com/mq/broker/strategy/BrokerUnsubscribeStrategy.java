package com.mq.broker.strategy;

import com.mq.broker.hook.ConsumerMessageListener;
import com.mq.broker.hook.ProducerMessageListener;
import com.mq.consumer.cluster.ConsumerContext;
import com.mq.model.message.UnSubscribeMessage;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * broker取消订阅：执行策略
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午3:01:29
 */
public class BrokerUnsubscribeStrategy implements BrokerStrategy {

    @Override
    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        UnSubscribeMessage msgUnSubscribe = (UnSubscribeMessage) request.getMsgParams();
        ConsumerContext.unLoad(msgUnSubscribe.getConsumerId());
    }

    @Override
    public void setHookProducer(ProducerMessageListener hookProducer) {

    }

    @Override
    public void setHookConsumer(ConsumerMessageListener hookConsumer) {

    }

    @Override
    public void setCtx(ChannelHandlerContext channelHandler) {

    }

}
