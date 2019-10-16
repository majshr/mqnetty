package com.mq.broker.strategy;

import com.mq.broker.hook.ConsumerMessageListener;
import com.mq.broker.hook.ProducerMessageListener;
import com.mq.model.RemoteChannelData;
import com.mq.model.message.SubscribeMessage;
import com.mq.model.message.msgenum.MessageType;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * 订阅策略
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 上午9:38:02
 */
public class BrokerSubscribeStrategy implements BrokerStrategy {

    private ConsumerMessageListener hookConsumer;
    private ChannelHandlerContext channelHandler;

    @Override
    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        SubscribeMessage subscribeMessage = (SubscribeMessage) request.getMsgParams();

        String clientKey = subscribeMessage.getConsumerId();
        RemoteChannelData channel = new RemoteChannelData(channelHandler.channel(), clientKey);

        hookConsumer.hookConsumerMessage(subscribeMessage, channel);
        response.setMsgType(MessageType.MQConsumerAck);
        channelHandler.writeAndFlush(response);
    }

    @Override
    public void setHookProducer(ProducerMessageListener hookProducer) {

    }

    @Override
    public void setHookConsumer(ConsumerMessageListener hookConsumer) {
        this.hookConsumer = hookConsumer;
    }

    @Override
    public void setCtx(ChannelHandlerContext channelHandler) {
        this.channelHandler = channelHandler;
    }

}
