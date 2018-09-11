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


}
