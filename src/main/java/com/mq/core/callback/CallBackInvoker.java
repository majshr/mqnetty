package com.mq.core.callback;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 回调
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 上午9:35:26
 */
public class CallBackInvoker<T> {
    static Logger LOG = LoggerFactory.getLogger(CallBackInvoker.class);

    /** 等待数据有结果同步器 */
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    /** 结果，响应消息 */
    private T messageResult;

    /** 监听者，等待有结果，进行回调 */
    private Queue<CallBackListener<T>> listeners = new ConcurrentLinkedQueue<CallBackListener<T>>();

    /** 请求id */
    private String requestId;

    /** 出问题了，问题原因 */
    private Throwable reason;

    /**
     * 设置问题原因信息
     * 
     * @param reason
     * @date: 2019年9月19日 下午2:55:28
     */
    public void setReason(Throwable reason) {
        this.reason = reason;

        publish();

        countDownLatch.countDown();
    }

    /**
     * 设置结果
     * 
     * @param messageResult
     * @date: 2019年9月19日 下午2:36:37
     */
    public void setMessageResult(T messageResult) {
        this.messageResult = messageResult;

        publish();

        countDownLatch.countDown();
    }

    /**
     * 获取结果
     * 
     * @param timeout
     * @param unit
     * @return Object
     * @date: 2019年9月19日 下午2:36:30
     */
    public Object getMessageResult(long timeout, TimeUnit unit) {
        if (messageResult != null) {
            return messageResult;
        }

        // 结果还未设置，等待结果
        try {
            // countDownLatch.await(timeout, unit);
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOG.error("等待中断===========", e);
        }

        // 出错了，出错原因不为空
        if (reason != null) {
            return null;
        }

        // 正确的
        return messageResult;
    }

    /**
     * 添加监听任务
     * 
     * @param listener
     * @date: 2019年9月19日 下午2:33:02
     */
    public void join(CallBackListener<T> listener) {
        this.listeners.add(listener);
    }

    /**
     * 回调所有监听者，发布结果
     * 
     * @date: 2019年9月19日 下午2:34:42
     */
    private void publish() {
        for (CallBackListener<T> listener : listeners) {
            listener.onCallBack(messageResult);
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public CallBackInvoker() {
    }
}
