package com.mq.netty.handler.consumer;

import com.mq.core.HookMessageEvent;
import com.mq.model.message.ConsumerAckMessage;
import com.mq.model.message.msgenum.MessageSource;
import com.mq.model.message.msgenum.MessageType;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;
import com.mq.netty.MessageProcessor;
import com.mq.netty.handler.MessageEventWrapper;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息消费者handler
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 下午5:50:51
 */
public class MessageConsumerHandler extends MessageEventWrapper<ResponseMessage, Object> {

    public MessageConsumerHandler(MessageProcessor processor) {
        this(processor, null);
    }

    public MessageConsumerHandler(MessageProcessor processor, HookMessageEvent<Object> hook) {
        super(processor, hook);
    }

    @Override
    public void beforeMessage(ResponseMessage msg) {
    }

    // @Override
    // public void afterMessage(Object msg) {
    //
    // }

    /**
     * channelRead中调用
     */
    @Override
    public void handleMessage(ChannelHandlerContext ctx, ResponseMessage msg) {
        String key = msg.getMsgId();

        // 消费者订阅信息，返回消息类型
        if (msg.getMsgType() == MessageType.MQConsumerAck) {
            return;
        }

        // 消费者获取订阅的信息，是直接获取的，没有请求
        if (msg.getMsgType() == MessageType.MQMessage && hookMessageEvent != null) {
            connectContext.traceInvoker(key);
            // 根据响应消息获取回调信息
            ResponseMessage message = msg;

            // 设置消费者获取应答消息
            ConsumerAckMessage result = (ConsumerAckMessage) hookMessageEvent.callBackMessage(message);
            if (result != null) {
                // 消费者订阅获取消息成功后，发送消费成功的响应，给broker
                RequestMessage request = new RequestMessage();
                request.setMsgId(message.getMsgId());
                request.setMsgSource(MessageSource.MQConsumer);
                // 请求消息
                request.setMsgType(MessageType.MQMessage);
                request.setMsgParams(result);

                ctx.writeAndFlush(request);
            }
        }
    }

}
