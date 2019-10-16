package com.mq.consumer.cluster;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mq.model.RemoteChannelData;
import com.mq.model.SubscriptionData;
import com.mq.util.NettyUtil;

/**
 * 消费者集群信息管理类（单个集群-》多个消费者，订阅相同主题）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 上午9:56:46
 */
public class ConsumerClusters {

    static final Logger LOG = LoggerFactory.getLogger(ConsumerClusters.class);

    private final String clustersId;

    /** key: topic字符串       value: 订阅主题对象 */
    private final ConcurrentHashMap<String, SubscriptionData> subMap = new ConcurrentHashMap<>();

    /** key: clientId（消费者标识编码）          value:通道信息 */
    private final ConcurrentHashMap<String, RemoteChannelData> channelMap = new ConcurrentHashMap<String, RemoteChannelData>();

    /** 通道信息（消费者）list，随机选取一个Channel时使用 */
    private final CopyOnWriteArrayList<RemoteChannelData> channelList = new CopyOnWriteArrayList<>();

    public ConsumerClusters(String clustersId) {
        this.clustersId = clustersId;
    }

    /**
     * 查找订阅的数据
     * 
     * @param topic
     * @return SubscriptionData
     * @date: 2019年9月24日 上午10:44:37
     */
    public SubscriptionData findSubscriptionData(String topic) {
        return this.subMap.get(topic);
    }

    /**
     * 查询客户端（消费者）信息
     * 
     * @param clientId
     * @return RemoteChannelData
     * @date: 2019年9月24日 上午10:47:02
     */
    public RemoteChannelData findRemoteChannelData(String clientId) {
        return this.channelMap.get(clientId);
    }

    /**
     * 添加客户端通道信息，即添加一个消费者到消费集群
     * 
     * @param clientId
     * @param channelinfo
     * @date: 2019年9月24日 上午10:48:18
     */
    public void attachRemoteChannelData(String clientId, RemoteChannelData channelinfo) {
        if(findRemoteChannelData(clientId) == null) {
            channelMap.put(clientId, channelinfo);
            subMap.put(channelinfo.getSubcript().getTopic(), channelinfo.getSubcript());
            channelList.add(channelinfo);
        } else {
            LOG.info("consumer clusters exists! it's clientId: " + clientId);
        }
    }

    /**
     * 移除clientId对应信息（从消费集群，移除一个消费者）
     * 
     * @param clientId
     * @date: 2019年9月24日 上午11:11:48
     */
    public void detachRemoteChannelData(String clientId) {
        // 移除客户端
        channelMap.remove(clientId);

        // 移除list中元素
        RemoteChannelData remoteChannelData = new RemoteChannelData(null, clientId);
        channelList.remove(remoteChannelData);

        // 移除主题？
    }

    /**
     * 随机选取一个客户端（消费者）信息
     * 
     * @return RemoteChannelData
     * @date: 2019年9月24日 上午11:15:15
     */
    public RemoteChannelData nextRemoteChannelData() {
        Random random = new Random();
        int num = random.nextInt(channelList.size());
        if (NettyUtil.validateChannel(channelList.get(num).getChannel())) {
            return channelList.get(num);
        } else {
            return nextRemoteChannelData();
        }
    }

    public String getClustersId() {
        return clustersId;
    }

    public ConcurrentHashMap<String, SubscriptionData> getSubMap() {
        return subMap;
    }

    public ConcurrentHashMap<String, RemoteChannelData> getChannelMap() {
        return channelMap;
    }

    public CopyOnWriteArrayList<RemoteChannelData> getChannelList() {
        return channelList;
    }


}
