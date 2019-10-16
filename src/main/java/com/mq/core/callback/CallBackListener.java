package com.mq.core.callback;

/**
 * 回调监听器接口
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月19日 下午2:20:49
 */
public interface CallBackListener<T> {
    void onCallBack(T t);
}
