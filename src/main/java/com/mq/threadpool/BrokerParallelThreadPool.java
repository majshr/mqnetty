package com.mq.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mq.config.SystemConfig;

public class BrokerParallelThreadPool {
    public static int parallel = SystemConfig.AvailableProcessors * 2 + 1;
    public static volatile ExecutorService executorService;

    public static ExecutorService getExecutorService() {
        if (executorService == null) {
            synchronized (BrokerParallelThreadPool.class) {
                if (executorService == null) {
                    executorService = Executors.newFixedThreadPool(parallel);
                }
            }
        }

        return executorService;
    }

    public static int getParallel() {
        return parallel;
    }

}
