package com.mq.core.queue;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mq.model.message.ProducerAckMessage;

/**
 * ack 消息任务队列（1，生产者上产消息，没有消费者订阅，直接应答为成功 2，生产者生产消息，有消费者消费，应答为成功）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午4:52:44
 */
public class AckTaskQueue {
    /***/
    private static ConcurrentLinkedQueue<ProducerAckMessage> ackQueue = new ConcurrentLinkedQueue<ProducerAckMessage>();

    /**
     * 添加消息
     * 
     * @param ack
     * @return boolean
     * @date: 2019年9月24日 下午4:53:25
     */
    public static boolean pushAck(ProducerAckMessage ack) {
        return ackQueue.offer(ack);
    }

    /**
     * 批量添加消息
     * 
     * @param acks
     * @return boolean
     * @date: 2019年9月24日 下午4:53:44
     */
    public static boolean pushAcks(List<ProducerAckMessage> acks) {
        boolean flag = false;
        for (ProducerAckMessage ack : acks) {
            flag = ackQueue.offer(ack);
        }
        return flag;
    }

    /**
     * 取出消息
     * 
     * @return ProducerAckMessage
     * @date: 2019年9月24日 下午4:54:11
     */
    public static ProducerAckMessage getAck() {
        return ackQueue.poll();
    }
}
