package com.mq.core;

/**
 * 动作接口：启动|初始化|停止
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月18日 下午5:45:52
 */
public interface MqAction {
    /**
     * 启动
     * 
     * @date: 2019年10月12日 上午10:25:55
     */
    void start();

    /**
     * 初始化
     * 
     * @date: 2019年10月12日 上午10:25:55
     */
    void init();

    /**
     * 停止
     * 
     * @date: 2019年10月12日 上午10:25:55
     */
    void shutdown();
}
