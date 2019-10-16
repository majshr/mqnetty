package com.mq.model.message;

import java.io.Serializable;

public class UnSubscribeMessage extends BaseMessage implements Serializable {
    private static final long serialVersionUID = 8809017923582098366L;

    private String consumerId;

    public UnSubscribeMessage(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public String toString() {
        return "UnSubscribeMessage [consumerId=" + consumerId + "]";
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
}
