package com.mq.broker.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mq.consumer.cluster.ConsumerContext;
import com.mq.model.RemoteChannelData;
import com.mq.model.SubscriptionData;
import com.mq.model.message.SubscribeMessage;

/**
 * 消费者订阅消息（hook响应），将消费者添加到集群管理
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午3:26:43
 */
public class ConsumerMessageHook implements ConsumerMessageListener {

    static final Logger LOG = LoggerFactory.getLogger(ConsumerMessageHook.class);

    @Override
    public void hookConsumerMessage(SubscribeMessage msg, RemoteChannelData channelData) {
        LOG.info("receive subcript info groupid: " + msg.getClusterId() + " topic: " + msg.getTopic() + " clientId: "
                + channelData.getClientId());

        // 订阅主题
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setTopic(msg.getTopic());

        channelData.setSubcript(subscriptionData);

        // 消费者集群管理，添加消费者到集群
        ConsumerContext.addClusters(msg.getClusterId(), channelData);
    }

}
