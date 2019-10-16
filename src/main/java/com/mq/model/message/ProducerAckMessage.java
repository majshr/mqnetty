package com.mq.model.message;

import java.io.Serializable;

/**
 * 生产者应答
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月18日 下午5:12:48
 */
public class ProducerAckMessage extends BaseMessage implements Serializable {
    private static final long serialVersionUID = -5399756346963858220L;

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
