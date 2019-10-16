package com.mq.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mq.config.SystemConfig;
import com.mq.core.callback.CallBackInvoker;
import com.mq.netty.codec.MessageObjectDecoder;
import com.mq.netty.codec.MessageObjectEncoder;
import com.mq.serialize.KryoCodecUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * 连接管理（连接方法，channel管理，请求回调管理，handler管理）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 下午2:33:30
 */
public class MessageConnectContext {
    static Logger LOG = LoggerFactory.getLogger(MessageConnectContext.class);

    /**
     * broker地址
     */
    private SocketAddress remoteAddr = null;

    /**
     * 保存所有请求消息id与响应回调信息对应关系
     */
    private Map<String, CallBackInvoker<Object>> callBackMap = new ConcurrentHashMap<String, CallBackInvoker<Object>>();

    /**
     * 编解码工具
     */
    private static KryoCodecUtil codecUtil = KryoCodecUtil.getInstance();

    /** netty配置 */
    /** 连接broker的通道 */
    private Channel messageChannel = null;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private EventLoopGroup workerGroup = null;
    private ChannelInboundHandlerAdapter messageHandler = null;
    private Bootstrap bootstrap = null;
    private long timeout = 10 * 1000;
    private boolean connected = false;

    private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("MessageConnectFactory-%d")
            .setDaemon(true).build();

    /**
     * 等待连接成功同步器
     */
    private CountDownLatch connectSuccessLatch = new CountDownLatch(1);

    /**
     * 根据服务地址构造
     * 
     * @param serverAddress
     */
    public MessageConnectContext(String serverAddress) {
        String[] ipAddr = serverAddress.split(SystemConfig.IpV4AddressDelimiter);
        if (ipAddr.length == 2) {
            remoteAddr = new InetSocketAddress(ipAddr[0], Integer.valueOf(ipAddr[1]));
        }
    }

    /**
     * 初始化netty客户端信息（初始化bootstrap，eventLoopGroup信息）
     * 
     * @return boolean
     * @date: 2019年9月20日 下午2:05:19
     */
    public boolean init() {
        try {
            defaultEventExecutorGroup = new DefaultEventExecutorGroup(SystemConfig.AvailableProcessors * 2 + 1,
                    threadFactory);
            workerGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(defaultEventExecutorGroup);
                            channel.pipeline().addLast(new MessageObjectDecoder(codecUtil));
                            channel.pipeline().addLast(new MessageObjectEncoder(codecUtil));
                            channel.pipeline().addLast(messageHandler);
                        }
                    }).option(ChannelOption.SO_SNDBUF, SystemConfig.SocketSndbufSize)
                    .option(ChannelOption.SO_RCVBUF, SystemConfig.SocketRcvbufSize)
                    .option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, false);
            return true;
        } catch (Exception e) {
            LOG.error("init错误!", e);
            return false;
        }
    }

    /**
     * 进行连接（连接服务端）
     * 
     * @date: 2019年9月20日 下午2:22:52
     */
    public void connect() {
        if (messageHandler == null) {
            LOG.error("messageHandler is null!");
            return;
        }

        // 初始化连接参数
        if (!init()) {
            return;
        }
        try {
            ChannelFuture channelFuture = bootstrap.connect(remoteAddr).sync();

            channelFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    LOG.info("connect to broker success!");
                    Channel channel = future.channel();
                    messageChannel = channel;
                    connected = true;
                    // 连接成功，唤醒
                    connectSuccessLatch.countDown();
                }
            });
        } catch (InterruptedException e) {
            LOG.error("中断!", e);
        }
    }

    /**
     * 是否建立好了连接
     * 
     * @return boolean
     * @date: 2019年9月20日 下午2:25:23
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * 查询key对应的CallBackInvoker<T> 是否存在
     * 
     * @param key
     * @return boolean
     * @date: 2019年9月20日 下午2:26:48
     */
    public boolean traceInvoker(String key) {
        if (key == null) {
            return false;
        }
        return getCallBackMap().containsKey(key);
    }

    /**
     * 删除key对应的CallBackInvoker<T>
     * 
     * @param key
     * @return CallBackInvoker<Object>
     * @date: 2019年9月20日 下午2:27:44
     */
    public CallBackInvoker<Object> detachInvoker(String key) {
        if (traceInvoker(key)) {
            return getCallBackMap().remove(key);
        } else {
            return null;
        }
    }

    /**
     * 关闭连接信息（channel，EventLoopGroup，）
     * 
     * @date: 2019年9月19日 下午5:51:02
     */
    public void close() {
        if (messageChannel != null) {
            try {
                messageChannel.close().sync();
                workerGroup.shutdownGracefully();
                defaultEventExecutorGroup.shutdownGracefully();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取连接channel；同步方法，如果没有值，一直阻塞
     * 
     * @return Channel
     * @date: 2019年10月12日 下午3:33:26
     */
    public Channel getMessageChannel() {
        while (messageChannel == null) {
            try {
                connectSuccessLatch.await();
            } catch (InterruptedException e) {
                LOG.error("等待连接被中断！", e);
            }
        }
        return messageChannel;
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.countDown();

        countDownLatch.await();
        System.out.println("aaa");

        countDownLatch.await();

        System.out.println("aaa");
    }

    public SocketAddress getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(SocketAddress remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public ChannelInboundHandlerAdapter getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(ChannelInboundHandlerAdapter messageHandler) {
        this.messageHandler = messageHandler;
    }

    public Map<String, CallBackInvoker<Object>> getCallBackMap() {
        return callBackMap;
    }

    public void setCallBackMap(Map<String, CallBackInvoker<Object>> callBackMap) {
        this.callBackMap = callBackMap;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public static KryoCodecUtil getCodecUtil() {
        return codecUtil;
    }

    public static void setCodecUtil(KryoCodecUtil codecUtil) {
        MessageConnectContext.codecUtil = codecUtil;
    }

    public void setMessageChannel(Channel messageChannel) {
        this.messageChannel = messageChannel;
    }

    public DefaultEventExecutorGroup getDefaultEventExecutorGroup() {
        return defaultEventExecutorGroup;
    }

    public void setDefaultEventExecutorGroup(DefaultEventExecutorGroup defaultEventExecutorGroup) {
        this.defaultEventExecutorGroup = defaultEventExecutorGroup;
    }

    public EventLoopGroup getEventLoopGroup() {
        return workerGroup;
    }

    public void setEventLoopGroup(EventLoopGroup eventLoopGroup) {
        this.workerGroup = eventLoopGroup;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
