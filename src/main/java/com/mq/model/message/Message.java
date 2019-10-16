package com.mq.model.message;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 生产者生产的消息
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月18日 下午5:10:25
 */
public class Message extends BaseMessage implements Serializable {
    private static final long serialVersionUID = -7380109331138893208L;

    private String msgId;
    private String topic;
    private byte[] body;
    private long timeStamp;


    @Override
    public String toString() {
        return "Message [msgId=" + msgId + ", topic=" + topic + ", body=" + Arrays.toString(body) + ", timeStamp="
                + timeStamp + "]";
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
