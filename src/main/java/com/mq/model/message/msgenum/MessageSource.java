package com.mq.model.message.msgenum;

/**
 * 消息来源
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月19日 下午2:47:19
 */
public enum MessageSource {
    MQConsumer(1), MQBroker(2), MQProducer(3);

    private int source;

    private MessageSource(int source) {
        this.source = source;
    }

    public int getSource() {
        return source;
    }
}
