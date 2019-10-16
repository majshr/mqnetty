package com.mq.netty.pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageConnectFactory对象池
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 上午9:33:48
 */
public class MessageConnectPool extends GenericObjectPool<MessageConnectFactory> {
    static Logger LOG = LoggerFactory.getLogger(MessageConnectPool.class);

    private volatile static MessageConnectPool pool = null;
    /**
     * 池单例
     * 
     * @return MessageConnectPool
     * @date: 2019年9月19日 下午5:26:28
     */
    public static MessageConnectPool getMessageConnectPoolInstance() {
        if (pool == null) {
            synchronized (MessageConnectPool.class) {
                if (pool == null) {
                    pool = new MessageConnectPool();
                }
            }
        }
        return pool;
    }

    private static Properties messageConnectConfigProperties = null;
    /**
     * 池配置信息
     */
    private static String configPropertiesPath = "mqnetty.messageconnect.properties";

    private static String serverAddress = "";

    static {
        // 加载配置文件，初始化properties
        initProperties();
    }
    
    /**
     * 构造方法
     */
    private MessageConnectPool() {
        super(new MessageConnectPoolableObjectFactory(serverAddress,
                Integer.parseInt(messageConnectConfigProperties.getProperty("sessionTimeOut"))));

        int maxActive = Integer.parseInt(messageConnectConfigProperties.getProperty("maxActive"));
        int minIdle = Integer.parseInt(messageConnectConfigProperties.getProperty("minIdle"));
        int maxIdle = Integer.parseInt(messageConnectConfigProperties.getProperty("maxIdle"));
        int maxWait = Integer.parseInt(messageConnectConfigProperties.getProperty("maxWait"));
        int sessionTimeOut = Integer.parseInt(messageConnectConfigProperties.getProperty("sessionTimeOut"));

        this.setMaxIdle(maxActive);
        this.setMaxIdle(maxIdle);
        this.setMinIdle(minIdle);
        this.setMaxWaitMillis(maxWait);
        this.setTestOnBorrow(false);
        this.setTestOnReturn(false);
        this.setTimeBetweenEvictionRunsMillis(10 * 1000);
        this.setNumTestsPerEvictionRun(maxActive + maxIdle);
        this.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        this.setTestWhileIdle(true);
    }


    /**
     * 初始化配置
     * 
     * void
     * 
     * @date: 2019年9月19日 下午5:29:01
     */
    private static void initProperties() {
        try {
            messageConnectConfigProperties = new Properties();

            InputStream inputStream = MessageConnectPool.class.getClassLoader()
                    .getResourceAsStream(configPropertiesPath);

            messageConnectConfigProperties.load(inputStream);
            inputStream.close();

            serverAddress = serverAddress;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象
     * 
     * @return MessageConnectFactory
     * @date: 2019年9月20日 下午2:49:25
     */
    public MessageConnectFactory borrow() {
        try {
            return borrowObject();
        } catch (Exception e) {
            LOG.error("获取对象错误！", e);
        }
        // 对象池中获取不到，自己创建
        return null;
    }

    /**
     * 对象返回到池中
     * 
     * @date: 2019年9月20日 下午3:27:37
     */
    public void restore(MessageConnectFactory messageConnectFactory) {
        returnObject(messageConnectFactory);
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static void setServerAddress(String serverAddress) {
        MessageConnectPool.serverAddress = serverAddress;
    }

}
