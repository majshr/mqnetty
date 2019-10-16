package com.mq.consumer.cluster;

import java.util.Objects;

/**
 * 集群信息（包含集群ID和集群中的client信息）
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午2:18:30
 */
public class ClustersRelation {
    /**
     * 集群ID
     */
    private String id;
    /**
     * 集群中的成员（消费者成员）
     */
    private ConsumerClusters clusters;

    public ClustersRelation() {
        super();
    }

    public ClustersRelation(String id, ConsumerClusters clusters) {
        super();
        this.id = id;
        this.clusters = clusters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ConsumerClusters getClusters() {
        return clusters;
    }

    public void setClusters(ConsumerClusters clusters) {
        this.clusters = clusters;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == getClass() && Objects.equals(getId(), ((ClustersRelation) obj).getId())) {
            return true;
        }

        return false;

    }
}
