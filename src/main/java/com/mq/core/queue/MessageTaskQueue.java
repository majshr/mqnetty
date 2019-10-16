package com.mq.core.queue;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mq.model.MessageDispatchTask;

/**
 * message信息任务队列（MessageDispatchTask的队列）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午5:12:32
 */
public class MessageTaskQueue {
    
    private static ConcurrentLinkedQueue<MessageDispatchTask> taskQueue = null;
    
    /**
     * 添加任务
     * 
     * @param task
     * @return boolean
     * @date: 2019年9月24日 下午5:19:15
     */
    public boolean pushTask(MessageDispatchTask task) {
        return taskQueue.offer(task);
    }

    /**
     * 批量添加任务
     * 
     * @param tasks
     * @return boolean
     * @date: 2019年9月24日 下午5:19:22
     */
    public boolean pushTask(List<MessageDispatchTask> tasks) {
        return taskQueue.addAll(tasks);
    }

    /**
     * 获取任务
     * 
     * @return MessageDispatchTask
     * @date: 2019年9月24日 下午5:19:32
     */
    public MessageDispatchTask getTask() {
        return taskQueue.poll();
    }

    /**单例*/
    private volatile static MessageTaskQueue taskQueueInstance = null;
    private static AtomicBoolean isInit = new AtomicBoolean(false);
    private MessageTaskQueue() {
    }

    public static MessageTaskQueue getInstance() {
        while (taskQueueInstance == null && isInit.compareAndSet(false, true)) {
            // 创建实例
            taskQueue = new ConcurrentLinkedQueue<MessageDispatchTask>();
            taskQueueInstance = new MessageTaskQueue();
        }

        return taskQueueInstance;
    }
}
