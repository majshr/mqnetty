package com.mq.netty.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * 创建池中对象元素的工厂
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月19日 下午5:52:38
 */
public class MessageConnectPoolableObjectFactory extends BasePooledObjectFactory<MessageConnectFactory> {

    private String serverAddress;
    /** 没有用 */
    private int sessionTimeOut = 3 * 1000;

    public MessageConnectPoolableObjectFactory(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public MessageConnectPoolableObjectFactory(String serverAddress, int sessionTimeOut) {
        this.serverAddress = serverAddress;
        this.sessionTimeOut = sessionTimeOut;
    }

    /**
     * 创建对象
     */
    @Override
    public MessageConnectFactory create() throws Exception {
        return new MessageConnectFactory(serverAddress);
    }

    /**
     * 
     */
    @Override
    public PooledObject<MessageConnectFactory> wrap(MessageConnectFactory obj) {
        return new DefaultPooledObject<MessageConnectFactory>(obj);
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<MessageConnectFactory> p) throws Exception {
        p.getObject().close();
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(int sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

}
