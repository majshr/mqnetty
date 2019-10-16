package com.mq.broker.strategy;

import java.util.HashMap;
import java.util.Map;

import com.mq.broker.hook.ConsumerMessageListener;
import com.mq.broker.hook.ProducerMessageListener;
import com.mq.model.message.msgenum.MessageSource;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息处理策略
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午5:11:37
 */
public class BrokerStrategyContext {
    /** 生产者请求消息处理策略 */
    public final static int MQProducerMessageStrategy = 1;
    /** 消费者请求消息处理策略 */
    public final static int MQConsumerMessageStrategy = 2;
    /** 订阅消息处理策略 */
    public final static int MQSubscribeStrategy = 3;
    /** 取消订阅消息处理策略 */
    public final static int MQUnsubscribeStrategy = 4;

    private RequestMessage request;
    private ResponseMessage response;
    private ChannelHandlerContext ctx;
    private ProducerMessageListener hookProducer;
    private ConsumerMessageListener hookConsumer;

    private BrokerStrategy strategy;

    /** 执行策略 */
    static Map<Integer, BrokerStrategy> strategyMap = new HashMap<>();
    static {
        // 生产者请求消息处理策略
        strategyMap.put(MQProducerMessageStrategy, new BrokerProducerMessageStrategy());
        // 消费者请求消息处理策略
        strategyMap.put(MQConsumerMessageStrategy, new BrokerConsumerMessageStrategy());
        // 订阅消息处理策略
        strategyMap.put(MQSubscribeStrategy, new BrokerSubscribeStrategy());
        // 取消订阅消息处理策略
        strategyMap.put(MQUnsubscribeStrategy, new BrokerUnsubscribeStrategy());
    }

    public BrokerStrategyContext(RequestMessage request, ResponseMessage response,
            ChannelHandlerContext channelHandler) {
        this.request = request;
        this.response = response;
        this.ctx = channelHandler;
    }

    /**
     * 执行
     * 
     * @date: 2019年9月24日 下午3:10:45
     */
    public void invoke() {
        switch (request.getMsgType()) {
        case MQMessage:
            strategy = (BrokerStrategy) strategyMap
                    .get(request.getMsgSource() == MessageSource.MQProducer ? MQProducerMessageStrategy
                            : MQConsumerMessageStrategy);
            break;
        case MQSubscribe:
            strategy = (BrokerStrategy) strategyMap.get(MQSubscribeStrategy);
            break;
        case MQUnsubscribe:
            strategy = (BrokerStrategy) strategyMap.get(MQUnsubscribeStrategy);
            break;
        default:
            break;
        }

        // 设置基础信息：ctx，consumer hook，producer hook
        strategy.setCtx(ctx);
        strategy.setHookConsumer(hookConsumer);
        strategy.setHookProducer(hookProducer);
        strategy.messageDispatch(request, response);
    }

    public void setHookProducer(ProducerMessageListener hookProducer) {
        this.hookProducer = hookProducer;
    }

    public void setHookConsumer(ConsumerMessageListener hookConsumer) {
        this.hookConsumer = hookConsumer;
    }
}
