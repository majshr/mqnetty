package com.mq.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mq.config.SystemConfig;

public class ThreadPoolUtil {
    public static ExecutorService exe = Executors.newFixedThreadPool(SystemConfig.AvailableProcessors * 2);

    public static ExecutorService getExecutor() {
        return exe;
    }
}
