package com.mq.start;

import org.springframework.context.ApplicationContext;

/**
 * 容器操作接口
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月18日 下午5:26:42
 */
public interface Container {
    public void start();

    public void stop();

    /**
     * 获取spring ioc容器
     * 
     * @return ApplicationContext
     * @date: 2019年9月18日 下午5:39:50
     */
    ApplicationContext getContext();
}
