package com.mq.broker.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mq.broker.hook.ConsumerMessageHook;
import com.mq.broker.hook.ProducerMessageHook;
import com.mq.config.SystemConfig;
import com.mq.netty.codec.MessageObjectDecoder;
import com.mq.netty.codec.MessageObjectEncoder;
import com.mq.netty.handler.server.MessageBrokerHandler;
import com.mq.serialize.KryoCodecUtil;
import com.mq.util.NettyUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * broker服务
 * 启动方法（先调用init，再调用start）
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月23日 下午4:03:22
 */
public class BrokerMqServer extends BrokerParallelServer {

    static final Logger LOG = LoggerFactory.getLogger(BrokerMqServer.class);

    private ThreadFactory threadBossFactory = new ThreadFactoryBuilder()
            .setNameFormat("MQBroker[BossSelector]-%d").setDaemon(true).build();

    private ThreadFactory threadWorkerFactory = new ThreadFactoryBuilder()
            .setNameFormat("MQBroker[WorkerSelector]-%d").setDaemon(true).build();

    /** 服务端口 */
    private int brokerServerPort = 0;
    private ServerBootstrap bootstrap;
    private SocketAddress serverIpAddr;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private MessageBrokerHandler handler;

    /**
     * 构造方法
     * 
     * @param serverAddress
     *            ip:post
     */
    public BrokerMqServer(String serverAddress) {
        String[] ipAddr = serverAddress.split(SystemConfig.IpV4AddressDelimiter);

        if (ipAddr.length == 2) {
            serverIpAddr = NettyUtil.string2SocketAddress(serverAddress);
        }
    }

    @Override
    public void init() {
        try {
            // 初始化线程池
            super.init();

            handler = new MessageBrokerHandler().withConsumerHook(new ConsumerMessageHook())
                    .withProducerHook(new ProducerMessageHook());

            bossGroup = new NioEventLoopGroup(1, threadBossFactory);
            workerGroup = new NioEventLoopGroup(parallel, threadWorkerFactory, NettyUtil.getNioSelectorProvider());

            KryoCodecUtil kryoCodecUtil = KryoCodecUtil.getInstance();

            bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, false)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_SNDBUF, SystemConfig.SocketSndbufSize)
                    .option(ChannelOption.SO_RCVBUF, SystemConfig.SocketRcvbufSize)
                    .handler(new LoggingHandler(LogLevel.INFO)).localAddress(serverIpAddr)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(defaultEventExecutorGroup, new MessageObjectEncoder(kryoCodecUtil),
                                    new MessageObjectDecoder(kryoCodecUtil), handler);
                        }
                    });

        } catch (IOException e) {
            LOG.error("服务端启动错误!", e);
        }
    }

    /**
     * 停止服务
     */
    @Override
    public void shutdown() {
        try {
            super.shutdown();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            defaultEventExecutorGroup.shutdownGracefully();
        } catch (Exception e) {
            LOG.error("关闭错误!", e);
        }
    }

    // 启动
    @Override
    public void start() {
        try {
            String ipAddress = NettyUtil.socketAddress2String(serverIpAddr);
            System.out.printf("broker server ip:[%s]\n", ipAddress);

            ChannelFuture channelFuture = this.bootstrap.bind().sync();

            InetSocketAddress addr = (InetSocketAddress) channelFuture.channel().localAddress();
            brokerServerPort = addr.getPort();

            super.start();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            LOG.error("中断！", ex);
        }
    }

    public ThreadFactory getThreadBossFactory() {
        return threadBossFactory;
    }

    public void setThreadBossFactory(ThreadFactory threadBossFactory) {
        this.threadBossFactory = threadBossFactory;
    }

    public ThreadFactory getThreadWorkerFactory() {
        return threadWorkerFactory;
    }

    public void setThreadWorkerFactory(ThreadFactory threadWorkerFactory) {
        this.threadWorkerFactory = threadWorkerFactory;
    }

    public int getBrokerServerPort() {
        return brokerServerPort;
    }

    public void setBrokerServerPort(int brokerServerPort) {
        this.brokerServerPort = brokerServerPort;
    }

    public ServerBootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(ServerBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public SocketAddress getServerIpAddr() {
        return serverIpAddr;
    }

    public void setServerIpAddr(SocketAddress serverIpAddr) {
        this.serverIpAddr = serverIpAddr;
    }

    public DefaultEventExecutorGroup getDefaultEventExecutorGroup() {
        return defaultEventExecutorGroup;
    }

    public void setDefaultEventExecutorGroup(DefaultEventExecutorGroup defaultEventExecutorGroup) {
        this.defaultEventExecutorGroup = defaultEventExecutorGroup;
    }

    public EventLoopGroup getBoss() {
        return bossGroup;
    }

    public void setBoss(EventLoopGroup boss) {
        this.bossGroup = boss;
    }

    public EventLoopGroup getWorkers() {
        return workerGroup;
    }

    public void setWorkers(EventLoopGroup workers) {
        this.workerGroup = workers;
    }

    public MessageBrokerHandler getHandler() {
        return handler;
    }

    public void setHandler(MessageBrokerHandler handler) {
        this.handler = handler;
    }

}
