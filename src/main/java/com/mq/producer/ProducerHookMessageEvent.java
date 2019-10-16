package com.mq.producer;

import com.mq.core.HookMessageEvent;

/**
 * 生产者回调hook
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月26日 下午2:20:41
 */
public class ProducerHookMessageEvent extends HookMessageEvent<String> {
    private boolean brokerConnect = false;
    private boolean running = false;

    public ProducerHookMessageEvent() {
        super();
    }

    @Override
    public void disconnect(String addr) {
        synchronized (this) {
            if (isRunning()) {
                setBrokerConnect(false);
            }
        }
    }

    public boolean isBrokerConnect() {
        return brokerConnect;
    }

    public void setBrokerConnect(boolean brokerConnect) {
        this.brokerConnect = brokerConnect;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
