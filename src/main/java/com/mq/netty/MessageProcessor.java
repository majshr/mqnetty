package com.mq.netty;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.mq.core.callback.CallBackInvoker;
import com.mq.core.callback.CallBackListener;
import com.mq.core.callback.NotifyCallback;
import com.mq.model.message.ProducerAckMessage;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * 消息处理器（channel通道连接，消息发送等相关信息的处理）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 下午2:31:28
 */
public class MessageProcessor {

    org.slf4j.Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    /** 连接工厂 */
    // private MessageConnectFactory messageConnectFactory;

    /** 连接工厂对象池 */
    // private MessageConnectPool messageConnectPool =
    // MessageConnectPool.getMessageConnectPoolInstance();

    private MessageConnectContext messageConnectFactory = null;

    /**
     * 构造方法，设置连接工厂信息
     * 
     * @param serverAddress
     */
    public MessageProcessor(String serverAddress) {
        // messageConnectPool.setServerAddress(serverAddress);
        this.messageConnectFactory = new MessageConnectContext(serverAddress);
    }

    /**
     * 生成连接工厂对象
     * 
     * @date: 2019年9月20日 下午3:34:19
     */
//    public void generateMessageConnectFactory() {
//        this.messageConnectFactory = messageConnectPool.borrow();
//    }

    /**
     * 关闭连接工厂（连接工厂放回池里）
     * 
     * @date: 2019年9月20日 下午3:31:14
     * 
     */
//    public void closeMessageConnectFactory() {
//        messageConnectPool.restore(messageConnectFactory);
//    }

    /**
     * 获取连接对象工厂
     * 
     * @return MessageConnectFactory
     * @date: 2019年9月20日 下午3:33:26
     */
//    public MessageConnectFactory getMessageConnectFactory() {
//        return messageConnectFactory;
//    }
    public MessageConnectContext getMessageConnectFactory() {
        return messageConnectFactory;
    }

    /**
     * 同步发送信息，同步获取请求结果
     * 
     * @param request
     * @date: 2019年9月20日 下午3:36:43
     */
    public Object sendMessageSync(RequestMessage request) {
        Channel channel = messageConnectFactory.getMessageChannel();
        if (channel == null) {
            return null;
        }

        Map<String, CallBackInvoker<Object>> callBackMap = messageConnectFactory.getCallBackMap();
        CallBackInvoker<Object> invoker = new CallBackInvoker<>();
        callBackMap.put(request.getMsgId(), invoker);
        invoker.setRequestId(request.getMsgId());

        // 发送请求；发送成功后，handler会获取响应，并设置到callBackMap中的invoker中
        ChannelFuture channelFuture = channel.writeAndFlush(request);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // 如果请求失败，设置失败信息
                if (!future.isSuccess()) {
                    invoker.setReason(future.cause());
                }
            }
        });

        // 获取结果（同步阻塞，等待响应）
        Object result = invoker.getMessageResult(messageConnectFactory.getTimeout(), TimeUnit.MILLISECONDS);
        // 本次请求响应结束，移除本地请求的回调信息
        callBackMap.remove(request.getMsgId());
        return result;
    }

    /**
     * 发送请求（异步），不等待结果
     * 
     * @param request
     *            void
     * @date: 2019年9月20日 下午4:35:47
     */
    public void sendMessageAsync(RequestMessage request) {
        Channel channel = messageConnectFactory.getMessageChannel();
        if (channel == null) {
            return;
        }

        Map<String, CallBackInvoker<Object>> callBackMap = messageConnectFactory.getCallBackMap();
        CallBackInvoker<Object> invoker = new CallBackInvoker<Object>();
        callBackMap.put(request.getMsgId(), invoker);
        invoker.setRequestId(request.getMsgId());

        ChannelFuture channelFuture;
        try {
            channelFuture = channel.writeAndFlush(request).sync();
            channelFuture.addListener(new ChannelFutureListener() {

                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        invoker.setReason(future.cause());
                    }
                }
            });
        } catch (InterruptedException ex) {
            LOG.error("错误", ex);
        }
    }

    /**
     * 发送请求（异步），回调监听
     * 
     * @param request
     * @param listener
     * @return Object
     * @date: 2019年9月20日 下午3:51:32
     */
    public void sendMessageAsync(RequestMessage request, NotifyCallback listener) {
        Channel channel = messageConnectFactory.getMessageChannel();
        if (channel == null) {
            return;
        }

        Map<String, CallBackInvoker<Object>> callBackMap = messageConnectFactory.getCallBackMap();
        CallBackInvoker<Object> invoker = new CallBackInvoker<>();
        callBackMap.put(request.getMsgId(), invoker);
        invoker.setRequestId(request.getMsgId());

        // 添加请求完成监听回调
        invoker.join(new CallBackListener<Object>() {

            @Override
            public void onCallBack(Object t) {
                // 获取响应消息
                ResponseMessage response = (ResponseMessage) t;
                // 根据响应消息，做相应处理
                listener.onEvent((ProducerAckMessage) response.getMsgParams());
            }
        });

        ChannelFuture channelFuture = channel.writeAndFlush(request);
        channelFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    invoker.setReason(future.cause());
                }
            }
        });
    }

}
