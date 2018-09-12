package com.hiddenshrineoffline;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ShrineEntity {
    @PrimaryKey
    private int pk;

    @ColumnInfo
    private int cluster_uid;

    @ColumnInfo
    private String shrine_uid;

    @ColumnInfo (typeAffinity = ColumnInfo.BLOB)
    private byte[] shrine_image;


    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getCluster_uid() {
        return cluster_uid;
    }

    public void setCluster_uid(int cluster_uid) {
        this.cluster_uid = cluster_uid;
    }

    public String getShrine_uid() {
        return shrine_uid;
    }

    public void setShrine_uid(String shrine_uid) {
        this.shrine_uid = shrine_uid;
    }

    public byte[] getShrine_image() {
        return shrine_image;
    }

    public void setShrine_image(byte[] shrine_image) {
        this.shrine_image = shrine_image;
    }

}
