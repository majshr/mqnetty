package com.mq.broker.disparch.cache;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息缓存，一个ConcurrentLinkedQueue （包括提交消息到缓存，分发消息等方法）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午5:55:49
 */
public abstract class MessageCache<T> {
    static Logger LOG = LoggerFactory.getLogger(MessageCache.class);

    private ConcurrentLinkedQueue<T> cache = new ConcurrentLinkedQueue<>();

    /**
     * 值为0的信号量；aquire获取时先阻塞，等调用release释放后，aquire获取成功；之后的获取，也会阻塞，直到有调用release方法
     */
    private Semaphore semaphore = new Semaphore(0);

    /**
     * 添加信息到缓存
     * 
     * @param id
     * @date: 2019年9月19日 上午9:59:45
     */
    public void appendMessage(T id) {
        cache.add(id);
        // 释放信号量，释放之后，会有一个线程可以获取
        semaphore.release();
    }

    /**
     * 提交缓存队列中的信息（分发消息）
     * 
     * @date: 2019年9月19日 上午10:06:33
     */
    public void commit() {
        commitMessage(cache);
    }

    /**
     * 提交缓存队列中的信息
     * 
     * @date: 2019年9月19日 上午10:06:33
     */
    public void commit(ConcurrentLinkedQueue<T> tasks) {
        commitMessage(tasks);
        // commitMessage(cache);
    }

    /**
     * 批量提交消息，分发消息
     * 
     * @param messages
     * @date: 2019年9月19日 上午10:04:21
     */
    public void commitMessage(ConcurrentLinkedQueue<T> messages) {
        LinkedList<T> list = new LinkedList<>(messages);
        messages.clear();

        if (!list.isEmpty()) {
            parallelDispatch(list);
            list.clear();
        }
    }

    /**
     * 获取semphore信号
     * 
     * @param timeout
     * @return boolean
     * @date: 2019年9月19日 上午10:11:53
     */
    public boolean hold(long timeout) {
        try {
            return semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.error("中断====", e);
            return false;
        }
    }

    /**
     * 计算：1，执行次数（任务数/工作线程数） 2，线程数
     * 
     * @param parallel
     *            并行线程数
     * @param sizeOfTasks
     *            任务数
     * @return Pair<Integer,Integer>
     * @date: 2019年9月19日 上午10:19:26
     */
    protected Pair<Integer, Integer> calculateBlocks(int parallel, int sizeOfTasks) {
        // 并行数>任务数：启动任务数个线程；并行数<任务数：启动并行数个线程
        int numberOfThreads = parallel > sizeOfTasks ? sizeOfTasks : parallel;

        // key为执行次数（任务数/线程数）；value为线程数
        Pair<Integer, Integer> pair = new MutablePair<Integer, Integer>(new Integer(sizeOfTasks / numberOfThreads),
                new Integer(numberOfThreads));
        return pair;
    }

    /**
     * 并发执行任务
     * 
     * @param list
     * @date: 2019年9月29日 下午3:43:21
     */
    protected abstract void parallelDispatch(LinkedList<T> list);

    public static void main(String[] args) {
        /*MessageCache ca = new MessageCache();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ca.semaphore.acquire();
                    System.out.println("aquire 成功了!");

                    ca.semaphore.acquire();
                    System.out.println("aquire 成功了!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("aaaaaaaaaaaaaaaaaa");
                }

                ca.semaphore.release();

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ca.semaphore.release();
            }
        }).start();*/
    }
}
