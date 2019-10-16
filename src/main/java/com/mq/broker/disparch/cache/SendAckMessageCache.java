package com.mq.broker.disparch.cache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mq.broker.disparch.task.AckMessageTask;
import com.mq.threadpool.ThreadPoolUtil;

/**
 * ack 消息分发（生产者生产消息，被消费者消费后，再响应给生产者ack）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年10月14日 上午11:20:28
 */
public class SendAckMessageCache extends MessageCache<String> {
    Logger LOG = LoggerFactory.getLogger(SendAckMessageCache.class);

    private long succTaskCount = 0;

    public long getSuccTaskCount() {
        return succTaskCount;
    }

    @Override
    protected void parallelDispatch(LinkedList<String> list) {
        List<Callable<Long>> tasks = new ArrayList<Callable<Long>>();
        List<Future<Long>> futureList = new ArrayList<Future<Long>>();
        int startPosition = 0;

        Pair<Integer, Integer> pair = calculateBlocks(list.size(), list.size());
        // 线程数
        int numberOfThreads = pair.getRight();
        // 执行次数
        int blocks = pair.getLeft();

        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        // 根据list里的消息，转换出任务信息
        for (int i = 0; i < numberOfThreads; i++) {
            String[] task = new String[blocks];
            System.arraycopy(list.toArray(), startPosition, task, 0, blocks);

            tasks.add(new AckMessageTask(barrier, task));
            startPosition += blocks;
        }

        try {
            futureList = ThreadPoolUtil.getExecutor().invokeAll(tasks);
        } catch (InterruptedException ex) {
            LOG.error("执行中断异常！", ex);
        }

        for (Future<Long> longFuture : futureList) {
            try {
                succTaskCount += longFuture.get();
            } catch (InterruptedException ex) {
                LOG.error("错误！", ex);
            } catch (ExecutionException ex) {
                LOG.error("错误！", ex);
            }
        }
    }

    /** 静态内部类单例 */
    private SendAckMessageCache() {

    }

    private static class AckMessageCacheHolder {
        public static SendAckMessageCache cache = new SendAckMessageCache();
    }

    public static SendAckMessageCache getInstance() {
        return AckMessageCacheHolder.cache;
    }
}
