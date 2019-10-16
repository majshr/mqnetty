package com.mq.model.message;

import java.io.Serializable;

/**
 * 消费者ack message
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年10月16日 上午11:15:18
 */
public class ConsumerAckMessage extends BaseMessage implements Serializable {
    private static final long serialVersionUID = 5073251977448961273L;
    private String ack;
    private Integer status;
    private String msgId;

    @Override
    public String toString() {
        return "ProducerAckMessage [ack=" + ack + ", status=" + status + ", msgId=" + msgId + "]";
    }

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
