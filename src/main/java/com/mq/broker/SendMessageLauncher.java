package com.mq.broker;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import com.mq.config.SystemConfig;
import com.mq.core.callback.CallBackInvoker;
import com.mq.model.message.msgnet.ResponseMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * 管理回调invoker；CallBackInvoker管理类
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月27日 下午5:36:22
 */
public class SendMessageLauncher {
    /**
     * 超时时间
     */
    private long timeout = SystemConfig.MessageTimeOutValue;
    /**
     * 跳表，带排序功能
     */
    public ConcurrentSkipListMap<String, CallBackInvoker<Object>> invokeMap = new ConcurrentSkipListMap<String, CallBackInvoker<Object>>();

    /**
     * 向通道发送信息，返回执行结果（同步方法）
     * 
     * @param channel
     * @param response
     * @return Object
     * @date: 2019年9月19日 下午2:59:01
     */
    public Object launcher(Channel channel, ResponseMessage response) {
        if (channel != null) {
            CallBackInvoker<Object> invoke = new CallBackInvoker<>();
            try {
                invokeMap.put(response.getMsgId(), invoke);
                invoke.setRequestId(response.getMsgId());

                // 写消息，添加监听器
                ChannelFuture channelFuture = channel.writeAndFlush(response);
                channelFuture.addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            invoke.setReason(future.cause());
                        }
                    }
                });

                Object result = invoke.getMessageResult(timeout, TimeUnit.MILLISECONDS);
                return result;
            } finally {
                invokeMap.remove(response.getMsgId());
            }
        }

        return null;
    }

    /**
     * 是否存在key对应的CallbackInvoker
     * 
     * @param key
     * @return boolean
     * @date: 2019年9月23日 下午6:06:05
     */
    public boolean trace(String key) {
        return invokeMap.containsKey(key);
    }

    public CallBackInvoker<Object> detach(String key) {
        return invokeMap.remove(key);
    }

    /** 单例 */
    private SendMessageLauncher() {

    }

    private static volatile SendMessageLauncher resource;

    public static SendMessageLauncher getInstance() {
        if (resource == null) {
            synchronized (SendMessageLauncher.class) {
                if (resource == null) {
                    resource = new SendMessageLauncher();
                }
            }
        }
        return resource;
    }

}
