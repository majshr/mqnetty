package com.mq.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mq.config.SystemConfig;

/**
 * 发送给消费者消息的ThreadPool
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年10月15日 下午3:47:41
 */
public class SendMessageThreadPool {
    public static ExecutorService exe = Executors.newFixedThreadPool(SystemConfig.AvailableProcessors * 2);

    public static ExecutorService getExecutor() {
        return exe;
    }
}
