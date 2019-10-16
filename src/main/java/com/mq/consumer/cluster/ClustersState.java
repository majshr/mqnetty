/**
 * Copyright (C) 2016 Newland Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mq.consumer.cluster;

/**
 * 集群状态
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月19日 下午5:03:56
 */
public class ClustersState {

    public static final int ERROR = 1;
    public static final int SUCCESS = 0;
    public static final int NETWORKERR = -1;

    /** 集群名称ID */
    private String clusters;

    /** 集群状态 */
    private int state;

    ClustersState() {

    }

    ClustersState(String clusters, int state) {
        this.clusters = clusters;
        this.state = state;
    }

    public String getClusters() {
        return clusters;
    }

    public void setClusters(String clusters) {
        this.clusters = clusters;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    // public boolean equals(Object obj) {
    // boolean result = false;
    // if (obj != null && ClustersState.class.isAssignableFrom(obj.getClass()))
    // {
    // ClustersState clusters = (ClustersState) obj;
    // result = new EqualsBuilder().append(clusters, clusters.getClusters())
    // .isEquals();
    // }
    // return result;
    // }
}
