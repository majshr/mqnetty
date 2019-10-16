package com.mq.model.message.msgnet;

import java.io.Serializable;

import com.mq.model.message.BaseMessage;
import com.mq.model.message.msgenum.MessageSource;
import com.mq.model.message.msgenum.MessageType;

/**
 * 消息
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月19日 下午2:49:11
 */
public class BusinessMessage implements Serializable {

    private static final long serialVersionUID = 1396595726685789693L;

    public final static int SUCCESS = 0;
    public final static int FAIL = 1;

    protected String msgId;

    /** 消息来源 */
    protected MessageSource msgSource;

    /** 消息类型： */
    protected MessageType msgType;

    /** 生产者生产的具体消息；消费者、生产者ack消息；订阅、取消订阅消息 */
    protected BaseMessage msgParams;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public BaseMessage getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(BaseMessage msgParams) {
        this.msgParams = msgParams;
    }

    public MessageSource getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }
}
