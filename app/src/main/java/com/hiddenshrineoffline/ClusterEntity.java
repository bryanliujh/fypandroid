package com.hiddenshrineoffline;

public class ClusterEntity {

    private int cluster_uid;
    private String cluster_color;

    public ClusterEntity(int cluster_uid, String cluster_color) {
        this.cluster_uid = cluster_uid;
        this.cluster_color = cluster_color;
    }

    public int getCluster_uid() {
        return cluster_uid;
    }

    public void setCluster_uid(int cluster_uid) {
        this.cluster_uid = cluster_uid;
    }

    public String getCluster_color() {
        return cluster_color;
    }

    public void setCluster_color(String cluster_color) {
        this.cluster_color = cluster_color;
    }
}
