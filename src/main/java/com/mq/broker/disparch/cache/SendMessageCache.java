package com.mq.broker.disparch.cache;

import java.util.LinkedList;
import java.util.concurrent.Phaser;

import com.mq.broker.disparch.task.SendSingleMessageTask;
import com.mq.model.MessageDispatchTask;
import com.mq.threadpool.SendMessageThreadPool;

/**
 * 消息发送cache
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年10月12日 下午5:58:15
 */
public class SendMessageCache extends MessageCache<MessageDispatchTask> {

    private Phaser phaser = new Phaser(0);

    private static SendMessageCache cache = new SendMessageCache();

    /**
     * 单例获取
     * 
     * @return SendMessageCache
     * @date: 2019年9月19日 上午10:44:13
     */
    public static SendMessageCache getInstance() {
        return cache;
    }

    @Override
    protected void parallelDispatch(LinkedList<MessageDispatchTask> list) {
        list.forEach(messageDispatchTask -> {
            SendMessageThreadPool.getExecutor().submit(new SendSingleMessageTask(messageDispatchTask));
        });
        

/*        
        Pair<Integer, Integer> pair = calculateBlocks(list.size(), list.size());
        // 线程数
        int numberOfThreads = pair.getRight();
        // 执行次数
        int blocks = pair.getLeft();

        for (int i = 0; i < numberOfThreads; i++) {
            MessageDispatchTask[] taskArr = new MessageDispatchTask[blocks];
            phaser.register();
            System.arraycopy(list.toArray(), startPosition, taskArr, 0, blocks);
            tasks.add(new SendMessageTask(phaser, taskArr));
            startPosition += blocks;
        }

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        for (Runnable task : tasks) {
            executor.submit(task);
        }
        */
    }

}
