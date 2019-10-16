package com.mq.core.callback;

import com.mq.model.message.ProducerAckMessage;

/**
 * 消息响应后的回调接口
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月20日 下午3:50:03
 */
public interface NotifyCallback {
    /**
     * 消息响应完成后的回调方法
     * 
     * @param result
     * @date: 2019年9月23日 下午3:07:03
     */
    void onEvent(ProducerAckMessage result);
}
