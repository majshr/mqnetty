package com.mq.model.message.msgenum;

/**
 * 消息类型：订阅消息；取消订阅消息；信息；生产者ack消息；消费者ack消息
 * 
 * @author mengaijun
 * @date: 2019年9月19日 下午2:43:10
 */
public enum MessageType {

    MQSubscribe(1), MQUnsubscribe(2), MQMessage(3), MQProducerAck(4), MQConsumerAck(5);

    private int messageType;

    private MessageType(int messageType) {
        this.messageType = messageType;
    }

    /**
     * 获取类型数字
     * 
     * @return int
     * @date: 2019年9月19日 下午2:44:24
     */
    public int getMessageType() {
        return this.messageType;
    }
}
