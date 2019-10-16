package com.mq.consumer.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.mq.model.RemoteChannelData;
import com.mq.model.SubscriptionData;

/**
 * 管理消费者集群
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月26日 下午5:33:05
 */
public class ConsumerContext {

    /**
     * 消费者集群 数组 
     * key：集群ID    value：集群ID对应的集群信息
     */
    private static final ConcurrentHashMap<String, ClustersRelation> relationMap = new ConcurrentHashMap<>();
    // private static final CopyOnWriteArrayList<ClustersRelation> relationArray
    // = new CopyOnWriteArrayList<ClustersRelation>();

    /**
     * 消费者集群状态 
     * key：集群ID    value：集群ID对应的集群的状态
     */
    private static final ConcurrentHashMap<String, ClustersState> stateMap = new ConcurrentHashMap<>();
    // private static final CopyOnWriteArrayList<ClustersState> stateArray = new
    // CopyOnWriteArrayList<ClustersState>();

    /**
     * 添加或更新ClustersState集群状态信息
     * 
     * @param clusters
     *            集群名称（集群ID）
     * @param state
     * @date: 2019年9月24日 下午2:23:36
     */
    public static void addOrUpdateClustersState(String clusters, int state) {
        stateMap.put(clusters, new ClustersState(clusters, state));
    }

    /**
     * 根据消费者集群id，clusters，获取消费集群状态
     * 
     * @param clusters
     * @return Integer
     * @date: 2019年9月24日 下午2:27:57
     */
    public static Integer getClustersState(String clusters) {
        ClustersState state = stateMap.get(clusters);
        if (state != null) {
            return state.getState();
        }

        return null;
    }

    /**
     * 获取clustersId对应集群
     * 
     * @param clustersId
     * @return ConsumerClusters
     * @date: 2019年9月24日 下午2:30:08
     */
    public static ConsumerClusters selectByClusters(String clustersId) {
        ClustersRelation clustersRelation = relationMap.get(clustersId);
        return clustersRelation == null ? null : clustersRelation.getClusters();
    }

    /**
     * 查找关注主题的消费者集群（可能有多个集群都订阅了topic）
     * 
     * @param topic
     * @return List<ConsumerClusters>
     * @date: 2019年9月24日 下午4:37:07
     */
    public static List<ConsumerClusters> selectByTopic(String topic) {
        List<ConsumerClusters> clusters = new ArrayList<ConsumerClusters>();

        relationMap.forEach((clusterId, relation) -> {
            ConcurrentHashMap<String, SubscriptionData> subscriptionMap = relation.getClusters()
                    .getSubMap();
            if (subscriptionMap.containsKey(topic)) {
                clusters.add(relation.getClusters());
            }
        });

        return clusters;
    }

    /**
     * 添加消费者到集群
     * 
     * @param clustersId
     *            集群ID
     * @param channelinfo
     * @date: 2019年9月24日 下午2:54:13
     */
    public static void addClusters(String clustersId, RemoteChannelData channelinfo) {
        ConsumerClusters manage = selectByClusters(clustersId);

        // 没有集群信息，添加集群
        if (manage == null) {
            // 集群信息
            ConsumerClusters newClusters = new ConsumerClusters(clustersId);
            // 集群中添加client
            newClusters.attachRemoteChannelData(channelinfo.getClientId(), channelinfo);
            relationMap.put(clustersId, new ClustersRelation(clustersId, newClusters));
        }
        // 有集群信息，有client（消费者）信息，重置client（消费者）信息
        else if (manage.findRemoteChannelData(channelinfo.getClientId()) != null) {
            manage.detachRemoteChannelData(channelinfo.getClientId());
            manage.attachRemoteChannelData(channelinfo.getClientId(), channelinfo);
        }
        // 有集群信息，没有client信息
        else {
            String topic = channelinfo.getSubcript().getTopic();
            boolean touchChannel = manage.getSubMap().containsKey(topic);
            if (touchChannel) { // 包含主题
                manage.attachRemoteChannelData(channelinfo.getClientId(), channelinfo);
            } else { // 不包含主题
                manage.getSubMap().clear();
                manage.getChannelMap().clear();
                manage.attachRemoteChannelData(channelinfo.getClientId(), channelinfo);
            }
        }
    }

    /**
     * 卸载client；从一个消费者集群中，移除一个消费者
     * 
     * @param clientId
     * @date: 2019年9月24日 下午2:56:35
     */
    public static void unLoad(String clientId) {

        List<String> emptyClusters = new ArrayList<>();
        relationMap.forEach((clusters, relation) -> {
            ConsumerClusters manage = relation.getClusters();

            if (manage.findRemoteChannelData(clientId) != null) {
                manage.detachRemoteChannelData(clientId);
            }

            if (manage.getChannelMap().size() == 0) {
                emptyClusters.add(clusters);
            }
        });

        // 移除空集群
        emptyClusters.forEach((clusters) -> {
            relationMap.remove(clusters);
        });

    }
}
