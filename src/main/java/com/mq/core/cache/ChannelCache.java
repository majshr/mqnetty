package com.mq.core.cache;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * requestId对应Channel通道的缓存
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午3:55:31
 */
public class ChannelCache {
    private static ConcurrentHashMap<String, Channel> producerMap = new ConcurrentHashMap<String, Channel>();

    /**
     * key：id value：channel存入map
     * 
     * @param requestId
     * @param channel
     * @date: 2019年10月14日 下午3:44:17
     */
    public static void pushRequest(String requestId, Channel channel) {
        producerMap.put(requestId, channel);
    }

    /**
     * 根据ID查询channel
     * 
     * @param requestId
     * @return Channel
     * @date: 2019年10月14日 下午3:44:44
     */
    public static Channel findChannel(String requestId) {
        Channel channel = producerMap.remove(requestId);
        return channel;
    }
}
