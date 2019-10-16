package com.mq.broker;

import java.util.concurrent.Callable;

import com.mq.config.SystemConfig;
import com.mq.core.cache.ChannelCache;
import com.mq.core.cache.SemaphoreCache;
import com.mq.core.queue.AckTaskQueue;
import com.mq.model.message.ProducerAckMessage;
import com.mq.model.message.msgenum.MessageSource;
import com.mq.model.message.msgenum.MessageType;
import com.mq.model.message.msgnet.ResponseMessage;
import com.mq.util.NettyUtil;

import io.netty.channel.Channel;

/**
 * broker发送给消费者ack信息；处理器
 * 1，生产者生产消息，没有消费者订阅；响应ack消息给生产者
 * 2，生产者生产消息，被消费者消费；响应ack消息给生产者
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月29日 下午5:26:42
 */
public class AckProducerMessageController implements Callable<Void> {

    private volatile boolean stoped = false;

    public void stop() {
        stoped = true;
    }

    public boolean isStoped() {
        return stoped;
    }

    @Override
    public Void call() {
        while (!stoped) {
            // AckTaskQueue 中每放入一个消息，会调用这里，就同行一次
            SemaphoreCache.acquire(SystemConfig.AckTaskSemaphoreValue);

            ProducerAckMessage ack = AckTaskQueue.getAck();
            // 请求ID和msgId信息相同
            String requestId = ack.getMsgId();

            Channel channel = ChannelCache.findChannel(requestId);
            if (NettyUtil.validateChannel(channel)) {
                ResponseMessage response = new ResponseMessage();
                response.setMsgId(requestId);
                response.setMsgSource(MessageSource.MQBroker);
                response.setMsgType(MessageType.MQProducerAck);
                response.setMsgParams(ack);

                channel.writeAndFlush(response);
            }
        }
        return null;
    }

}
