package com.mq.netty.handler.producer;

import com.mq.core.HookMessageEvent;
import com.mq.core.callback.CallBackInvoker;
import com.mq.model.message.msgnet.ResponseMessage;
import com.mq.netty.MessageProcessor;
import com.mq.netty.handler.MessageEventWrapper;

import io.netty.channel.ChannelHandlerContext;

/**
 * 生产者handler
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月26日 下午2:23:32
 */
public class MessageProducerHandler extends MessageEventWrapper<ResponseMessage, String> {
    // private String key;

    public MessageProducerHandler(MessageProcessor processor) {
        this(processor, null);
    }

    public MessageProducerHandler(MessageProcessor processor, HookMessageEvent<String> hook) {
        super(processor, hook);
    }

    @Override
    public void beforeMessage(ResponseMessage msg) {

    }

    @Override
    public void handleMessage(ChannelHandlerContext ctx, ResponseMessage msg) {
        String key = ((ResponseMessage) msg).getMsgId();

        if (!connectContext.traceInvoker(key)) {
            return;
        }

        CallBackInvoker<Object> invoker = connectContext.detachInvoker(key);

        if (invoker == null) {
            return;
        }

        // 设置响应信息
        if (this.getCause() != null) {
            invoker.setReason(this.getCause());
        } else {
            invoker.setMessageResult(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 回调设置关闭状态（仅设置了标志位）
        if (hookMessageEvent != null) {
            hookMessageEvent.disconnect(ctx.channel().remoteAddress().toString());
        }
        super.channelInactive(ctx);
    }
}
