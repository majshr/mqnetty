package com.mq.consumer;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.mq.config.SystemConfig;
import com.mq.consumer.hook.ConsumeProducerMessageHook;
import com.mq.core.MessageIdGenerator;
import com.mq.core.MqAction;
import com.mq.model.message.SubscribeMessage;
import com.mq.model.message.UnSubscribeMessage;
import com.mq.model.message.msgenum.MessageType;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.netty.MessageProcessor;
import com.mq.netty.handler.consumer.MessageConsumerHandler;

/**
 * 消费者（执行流程：new创建对象，set设置信息，init，start，shutdown）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月19日 下午5:07:20
 */
public class MqConsumer extends MessageProcessor implements MqAction {
    /** 消费消息的hook */
    private ConsumeProducerMessageHook hook;

    /** broker地址 */
    private String brokerServerAddress;

    /** 消费的主题 */
    private String topic;

    /** 是否订阅 */
    private boolean subscribeMessage = false;

    /** 是否在运行中 */
    private boolean running = false;

    /** broker id */
    private String defaultClusterId = "MQConsumerClusters";

    /** broker id */
    private String clusterId = "";

    /** 消费者id */
    private String consumerId = "";

    public MqConsumer(String brokerServerAddress, String topic, ConsumeProducerMessageHook hook) {
        // 生成连接对象
        super(brokerServerAddress);

        this.hook = hook;
        this.brokerServerAddress = brokerServerAddress;
        this.topic = topic;
    }

    /**
     * 注册订阅相关信息（发送订阅消息给broker，表明自己是消费者）
     * 
     * @date: 2019年9月20日 下午4:44:31
     */
    private void register() {
        // 请求消息
        RequestMessage request = new RequestMessage();
        request.setMsgType(MessageType.MQSubscribe);
        request.setMsgId(MessageIdGenerator.UUID.generate());

        // 订阅消息（包括订阅的主题，订阅的broker，消费者id）
        SubscribeMessage subscribe = new SubscribeMessage();
        subscribe.setClusterId(StringUtils.isEmpty(clusterId) ? defaultClusterId : clusterId);
        subscribe.setTopic(topic);
        subscribe.setConsumerId(consumerId);

        request.setMsgParams(subscribe);

        sendMessageAsync(request);

        running = true;
    }

    /**
     * 取消订阅（停止消费者）
     * 
     * @date: 2019年9月20日 下午5:00:54
     */
    private void unRegister() {
        RequestMessage request = new RequestMessage();
        request.setMsgType(MessageType.MQUnsubscribe);
        request.setMsgId(MessageIdGenerator.UUID.generate());
        request.setMsgParams(new UnSubscribeMessage(consumerId));

        // 发送消息
        sendMessageAsync(request);

        // 关闭连接资源
        super.getMessageConnectFactory().close();
        // 返回对象给池
        // super.closeMessageConnectFactory();

        running = false;
    }

    // 连接到broker
    @Override
    public void start() {
        if (isSubscribeMessage()) {
            // 建立连接
            getMessageConnectFactory().connect();
            // 注册订阅，发送订阅请求
            register();
        }

    }

    // 初始化
    @Override
    public void init() {
        // 设置handler信息
        MessageConsumerHandler handler = new MessageConsumerHandler(this, new ConsumerHookMessageEvent(hook));
        getMessageConnectFactory().setMessageHandler(handler);

        Joiner joiner = Joiner.on(SystemConfig.MessageDelimiter).skipNulls();
        consumerId = joiner.join((clusterId.equals("") ? defaultClusterId : clusterId), topic,
                MessageIdGenerator.UUID.generate());

    }

    @Override
    public void shutdown() {
        if (running) {
            unRegister();
        }

    }

    public ConsumeProducerMessageHook getHook() {
        return hook;
    }

    public void setHook(ConsumeProducerMessageHook hook) {
        this.hook = hook;
    }

    public String getBrokerServerAddress() {
        return brokerServerAddress;
    }

    public void setBrokerServerAddress(String brokerServerAddress) {
        this.brokerServerAddress = brokerServerAddress;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isSubscribeMessage() {
        return subscribeMessage;
    }

    public void setSubscribeMessage(boolean subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getDefaultClusterId() {
        return defaultClusterId;
    }

    public void setDefaultClusterId(String defaultClusterId) {
        this.defaultClusterId = defaultClusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

}
