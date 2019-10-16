package com.mq.netty.handler;

import com.mq.core.HookMessageEvent;
import com.mq.netty.MessageConnectContext;
import com.mq.netty.MessageEventHandler;
import com.mq.netty.MessageEventProxy;
import com.mq.netty.MessageProcessor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 消息时间包装器
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午3:24:06
 */
public class MessageEventWrapper<T, M> extends SimpleChannelInboundHandler<T>
        implements MessageEventHandler<T>, MessageEventProxy<T> {

    /** 消息处理器 */
    protected MessageProcessor processor;
    /** 错误原因 */
    protected Throwable cause;
    /** hook */
    protected HookMessageEvent<M> hookMessageEvent;
    /** 消息连接管理工厂 */
    // protected MessageConnectFactory factory;


    protected MessageConnectContext connectContext;

    public MessageEventWrapper() {

    }

    public MessageEventWrapper(MessageProcessor processor) {
        this(processor, null);
    }

    public MessageEventWrapper(MessageProcessor processor, HookMessageEvent<M> hookMessageEvent) {
        this.processor = processor;
        this.hookMessageEvent = hookMessageEvent;
        this.connectContext = processor.getMessageConnectFactory();
    }

    @Override
    public void beforeMessage(T msg) {

    }

    @Override
    public void afterMessage(T msg) {

    }

    @Override
    public void handleMessage(ChannelHandlerContext ctx, T msg) {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.cause = cause;
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        // super.channelRead(ctx, msg);

        // 处理消息
        beforeMessage(msg);
        handleMessage(ctx, msg);
        afterMessage(msg);
    }

    public MessageProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(MessageProcessor processor) {
        this.processor = processor;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public HookMessageEvent<?> getHookMessageEvent() {
        return hookMessageEvent;
    }

    public void setHookMessageEvent(HookMessageEvent<M> hookMessageEvent) {
        this.hookMessageEvent = hookMessageEvent;
    }

    public MessageConnectContext getFactory() {
        return connectContext;
    }

    public void setFactory(MessageConnectContext factory) {
        this.connectContext = factory;
    }

}
