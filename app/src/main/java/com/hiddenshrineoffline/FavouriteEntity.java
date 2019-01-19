package com.hiddenshrineoffline;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class FavouriteEntity {
    @PrimaryKey(autoGenerate = true)
    private int pk;

    @ColumnInfo
    private int cluster_uid;

    @ColumnInfo
    private String shrine_uid;

    @ColumnInfo
    private String shrine_name;

    @ColumnInfo
    private String shrine_status;

    @ColumnInfo
    private String shrine_size;

    @ColumnInfo
    private String shrine_materials;

    @ColumnInfo
    private String shrine_deity;

    @ColumnInfo
    private String shrine_religion;

    @ColumnInfo
    private String shrine_offerings;

    @ColumnInfo
    private String shrine_imageURL;



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

    public String getShrine_name() {
        return shrine_name;
    }

    public void setShrine_name(String shrine_name) {
        this.shrine_name = shrine_name;
    }

    public String getShrine_status() {
        return shrine_status;
    }

    public void setShrine_status(String shrine_status) {
        this.shrine_status = shrine_status;
    }

    public String getShrine_size() {
        return shrine_size;
    }

    public void setShrine_size(String shrine_size) {
        this.shrine_size = shrine_size;
    }

    public String getShrine_materials() {
        return shrine_materials;
    }

    public void setShrine_materials(String shrine_materials) {
        this.shrine_materials = shrine_materials;
    }

    public String getShrine_deity() {
        return shrine_deity;
    }

    public void setShrine_deity(String shrine_deity) {
        this.shrine_deity = shrine_deity;
    }

    public String getShrine_religion() {
        return shrine_religion;
    }

    public void setShrine_religion(String shrine_religion) {
        this.shrine_religion = shrine_religion;
    }

    public String getShrine_offerings() {
        return shrine_offerings;
    }

    public void setShrine_offerings(String shrine_offerings) {
        this.shrine_offerings = shrine_offerings;
    }

    public String getShrine_imageURL() {
        return shrine_imageURL;
    }

    public void setShrine_imageURL(String shrine_imageURL) {
        this.shrine_imageURL = shrine_imageURL;
    }

}
