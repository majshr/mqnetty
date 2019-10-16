package com.mq.broker.disparch.task;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Splitter;
import com.mq.config.SystemConfig;
import com.mq.core.cache.SemaphoreCache;
import com.mq.core.queue.AckTaskQueue;
import com.mq.model.message.ProducerAckMessage;

/**
 * ack 应答生产者 Callable任务
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午5:44:23
 */
public class AckMessageTask implements Callable<Long> {
    CyclicBarrier barrier = null;
    String[] messages = null;
    private final AtomicLong count = new AtomicLong(0);

    public AckMessageTask(CyclicBarrier barrier, String[] messages) {
        this.barrier = barrier;
        this.messages = messages;
    }

    @Override
    public Long call() throws Exception {
        for (int i = 0; i < messages.length; i++) {
            // 应答生产者ack消息
            ProducerAckMessage ack = new ProducerAckMessage();

            // 消息格式，以@分隔字符串
            Object[] msg = Splitter.on(SystemConfig.MessageDelimiter).trimResults().splitToList(messages[i])
                    .toArray();
            
            if (msg.length == 2) {
                ack.setAck((String) msg[0]);
                ack.setMsgId((String) msg[1]);

                ack.setStatus(ProducerAckMessage.SUCCESS);
                count.incrementAndGet();

                // 添加ack信息到队列
                AckTaskQueue.pushAck(ack);

                // 释放信号量
                SemaphoreCache.release(SystemConfig.AckTaskSemaphoreValue);
            }
        }

        barrier.await();
        return count.get();
    }

}
