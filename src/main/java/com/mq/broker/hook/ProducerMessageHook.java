package com.mq.broker.hook;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.mq.broker.disparch.cache.SendAckMessageCache;
import com.mq.config.SystemConfig;
import com.mq.consumer.cluster.ConsumerClusters;
import com.mq.consumer.cluster.ConsumerContext;
import com.mq.core.cache.ChannelCache;
import com.mq.core.cache.SemaphoreCache;
import com.mq.core.queue.AckTaskQueue;
import com.mq.core.queue.MessageTaskQueue;
import com.mq.model.MessageDispatchTask;
import com.mq.model.message.Message;
import com.mq.model.message.ProducerAckMessage;

import io.netty.channel.Channel;

/**
 * 生产者消息hook（消息处理）
 *  消息未被订阅，ack生产者，消息没被消费
 *  消息被订阅者消费，分发消息，ack生产者，消息被消费
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午3:50:22
 */
public class ProducerMessageHook implements ProducerMessageListener {
    static Logger LOG = LoggerFactory.getLogger(ProducerMessageHook.class);

    public ProducerMessageHook() {

    }

    @Override
    public void hookProducerMessage(Message msg, String requestId, Channel channel) {
        // 请求ID，channel存入map
        ChannelCache.pushRequest(requestId, channel);

        String topic = msg.getTopic();

        List<ConsumerClusters> clustersList = ConsumerContext.selectByTopic(topic);

        // 如果有消费者订阅，进行分发
        if (checkClustersSet(msg, requestId, clustersList)) {

            // 分发消息给消费者
            dispatchTask(msg, topic, clustersList);

            /***********************
            // ack生产者
            taskAck(msg, requestId);
            ************************/
            
            // 简单点的方式，上边是原来的方式
            taskAckSimple(msg, requestId);

        } else {
            return;
        }

    }

    /**
     * ack消息设置，检查
     * 
     * @param msg
     * @param requestId
     * @param clustersList
     * @return boolean
     * @date: 2019年9月24日 下午5:09:08
     */
    private boolean checkClustersSet(Message msg, String requestId, List<ConsumerClusters> clustersList) {
        if (clustersList.size() == 0) {
            LOG.info("MQ don't have match clusters----没有订阅此主题的消费者!");

            // ack成功信息
            ProducerAckMessage ack = new ProducerAckMessage();
            ack.setMsgId(msg.getMsgId());
            ack.setAck("没人订阅此主题的消息！");
            ack.setStatus(ProducerAckMessage.SUCCESS);

            // 添加ack消息入队列
            AckTaskQueue.pushAck(ack);

            // 释放ack消息信号量（等待执行ack的线程同行）
            SemaphoreCache.release(SystemConfig.AckTaskSemaphoreValue);

            return false;
        } else {
            return true;
        }
    }

    /**
     * 分发消息（MessageDispatchTask任务添加到队列，相当于发送给消费者）
     * 
     * @param msg
     * @param topic
     * @param clustersList
     * @date: 2019年9月24日 下午5:10:09
     */
    private void dispatchTask(Message msg, String topic, List<ConsumerClusters> clustersList) {
        List<MessageDispatchTask> tasks = new ArrayList<MessageDispatchTask>(clustersList.size());

        // 消费转发给多个集群
        for (int i = 0; i < clustersList.size(); i++) {
            MessageDispatchTask task = new MessageDispatchTask();
            task.setClusters(clustersList.get(i).getClustersId());
            task.setTopic(topic);
            task.setMessage(msg);

            tasks.add(task);
        }

        // 添加到消息队列
        MessageTaskQueue.getInstance().pushTask(tasks);

        for (int i = 0; i < tasks.size(); i++) {
            // 释放notify对应信号量
            SemaphoreCache.release(SystemConfig.NotifyTaskSemaphoreValue);
        }
    }

    /**
     * ack信息添加到AckMessageCache中
     * 
     * 
     * @param msg
     * @param requestId
     *            void
     * @date: 2019年9月24日 下午5:21:37
     */
    private void taskAck(Message msg, String requestId) {
        try {
            Joiner joiner = Joiner.on(SystemConfig.MessageDelimiter).skipNulls();
            // 请求ID @ 消息ID
            String key = joiner.join(requestId, msg.getMsgId());
            SendAckMessageCache.getInstance().appendMessage(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 应答生产者，简单点的方式，直接应答
     * 
     * 
     * @param msg
     * @param requestId
     * @date: 2019年9月24日 下午5:21:37
     */
    private void taskAckSimple(Message msg, String requestId) {
        LOG.info("消息已被消费者消费!");

        // ack成功信息
        ProducerAckMessage ack = new ProducerAckMessage();
        ack.setMsgId(msg.getMsgId());
        ack.setAck("消息被成功消费！");
        ack.setStatus(ProducerAckMessage.SUCCESS);

        // 添加ack消息入队列
        AckTaskQueue.pushAck(ack);

        // 释放ack消息信号量（等待执行ack的线程同行）
        SemaphoreCache.release(SystemConfig.AckTaskSemaphoreValue);
    }

}
