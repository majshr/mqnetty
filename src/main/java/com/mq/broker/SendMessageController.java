package com.mq.broker;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mq.broker.disparch.cache.SendMessageCache;
import com.mq.config.SystemConfig;
import com.mq.core.cache.SemaphoreCache;
import com.mq.core.queue.MessageTaskQueue;
import com.mq.model.MessageDispatchTask;

/**
 * 将消息发送给消费者 controller
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月29日 下午5:14:56
 */
public class SendMessageController implements Callable<Void> {

    private volatile boolean stoped = false;

    private AtomicBoolean flushTask = new AtomicBoolean(false);

    private final Timer timer = new Timer("SendMessageTaskMonitor", true);

    private ThreadLocal<ConcurrentLinkedQueue<MessageDispatchTask>> taskQueueThreadLocal = new ThreadLocal<ConcurrentLinkedQueue<MessageDispatchTask>>() {
        protected ConcurrentLinkedQueue<MessageDispatchTask> initialValue() {
            // 新建一个线程安全的队列
            return new ConcurrentLinkedQueue<MessageDispatchTask>();
        }
    };

    @Override
    public Void call() throws Exception {
        // 周期时间
        int period = SystemConfig.SendMessageControllerPeriodTimeValue;
        int commitNumber = SystemConfig.SendMessageControllerTaskCommitValue;
        int sleepTime = SystemConfig.SendMessageControllerTaskSleepTimeValue;

        ConcurrentLinkedQueue<MessageDispatchTask> queue = taskQueueThreadLocal.get();

        SendMessageCache sendMessageCache = SendMessageCache.getInstance();
        
        while (!stoped) {
            SemaphoreCache.acquire(SystemConfig.NotifyTaskSemaphoreValue);

            MessageDispatchTask task = MessageTaskQueue.getInstance().getTask();
            if (task == null) {
                Thread.sleep(sleepTime);
                continue;
            }

            queue.add(task);
            
            // 达到提交数目，再提交
            if (queue.size() > 0 && (queue.size() % commitNumber == 0 || flushTask.get() == true)) {
                sendMessageCache.commit(queue);
                queue.clear();
                flushTask.compareAndSet(true, false);
            }

            timer.scheduleAtFixedRate(new TimerTask() {

                public void run() {
                    try {
                        flushTask.compareAndSet(false, true);
                    } catch (Exception e) {
                        System.out.println("SendMessageTaskMonitor happen exception");
                    }
                }
            }, 1000 * 1, period);
        }
        return null;
    }

}
