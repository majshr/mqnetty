package com.mq.model;

import java.io.Serializable;

import com.mq.model.message.Message;

/**
 * 消息分发任务（包括：集群ID，主题，消息对象；消息被发给指定集群）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月29日 上午10:37:15
 */
public class MessageDispatchTask implements Serializable {

    private static final long serialVersionUID = -5060471574960823376L;

    /**
     * 集群ID
     */
    private String clusters;

    /**
     * 主题
     */
    private String topic;

    /**
     * 消息
     */
    private Message message;

    public String getClusters() {
        return clusters;
    }

    public void setClusters(String clusters) {
        this.clusters = clusters;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}