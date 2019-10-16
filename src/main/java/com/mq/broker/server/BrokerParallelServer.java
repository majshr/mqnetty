package com.mq.broker.server;

import java.util.concurrent.ExecutorCompletionService;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mq.broker.AckProducerMessageController;
import com.mq.broker.SendMessageController;
import com.mq.threadpool.BrokerParallelThreadPool;

/**
 * 并发处理
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年10月14日 下午5:39:52
 */
public class BrokerParallelServer implements BrokerServer {

    protected int parallel = BrokerParallelThreadPool.getParallel();

    /** 业务线程池 */
    ListeningExecutorService executor = MoreExecutors.listeningDecorator(BrokerParallelThreadPool.getExecutorService());

    /** future的线程池 */
    ExecutorCompletionService<Void> executorCompletionService;

    @Override
    public void start() {
        for (int i = 0; i < parallel; i++) {
            executorCompletionService.submit(new SendMessageController());
            // executorCompletionService.submit(new AckPullMessageController());
            executorCompletionService.submit(new AckProducerMessageController());
        }
    }

    /**
     * 初始化
     */
    @Override
    public void init() {
        executorCompletionService = new ExecutorCompletionService<>(executor);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

}
