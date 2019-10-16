package com.mq.core;

/**
 * hook抽象类（两个方法：disconnect，callBackMessage）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 下午5:31:14
 */
public abstract class HookMessageEvent<T> {

    /**
     * 取消连接
     * 
     * @param message
     * @date: 2019年9月23日 下午3:17:55
     */
    public void disconnect(T message) {
    }

    /**
     * 回调消息
     * 
     * @param message
     * @return T
     * @date: 2019年9月23日 下午3:17:43
     */
    public T callBackMessage(T message) {
        return null;
    }

}
