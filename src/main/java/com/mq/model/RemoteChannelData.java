package com.mq.model;

import com.google.common.base.Objects;

import io.netty.channel.Channel;

/**
 * 远程通道的数据（消费者ID，Channel，subcript）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午5:00:36
 */
public class RemoteChannelData {
    private Channel channel;
    /** 消费者id */
    private String clientId;

    /** 订阅信息 */
    private SubscriptionData subcript;

    public SubscriptionData getSubcript() {
        return subcript;
    }

    public void setSubcript(SubscriptionData subcript) {
        this.subcript = subcript;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getClientId() {
        return clientId;
    }

    public RemoteChannelData(Channel channel, String clientId) {
        this.channel = channel;
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        RemoteChannelData data = (RemoteChannelData) obj;
        return Objects.equal(getClientId(), data.getClientId());
    }

    @Override
    public int hashCode() {
        return clientId == null ? "".hashCode() : clientId.hashCode();
    }
}
