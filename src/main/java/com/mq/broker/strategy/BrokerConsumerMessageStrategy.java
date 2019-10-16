package com.mq.broker.strategy;

import com.mq.broker.SendMessageLauncher;
import com.mq.broker.hook.ConsumerMessageListener;
import com.mq.broker.hook.ProducerMessageListener;
import com.mq.core.callback.CallBackInvoker;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * 接收到消费者发送ack确认消息：消息处理策略
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午6:03:15
 */
public class BrokerConsumerMessageStrategy implements BrokerStrategy {

    @Override
    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        // 确认消息被消费，设置消费结果，移除这个消息
        String key = response.getMsgId();
        if (SendMessageLauncher.getInstance().trace(key)) {
            CallBackInvoker<Object> future = SendMessageLauncher.getInstance().detach(key);
            if (future != null) {
                future.setMessageResult(request);
            }
        }
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
