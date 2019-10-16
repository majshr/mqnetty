package com.mq.model.message;

import java.io.Serializable;

/**
 * 订阅消息
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月18日 下午5:15:56
 */
public class SubscribeMessage extends BaseMessage implements Serializable {
    private static final long serialVersionUID = 5040564642391200098L;

    /**
     * 消费者集群 id
     */
    private String clusterId;
    /**
     * 主题
     */
    private String topic;
    /**
     * 消费者id
     */
    private String consumerId;

    @Override
    public String toString() {
        return "SubscribeMessage [clusterId=" + clusterId + ", topic=" + topic + ", consumerId=" + consumerId + "]";
    }

    public SubscribeMessage() {
        super();
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
}
