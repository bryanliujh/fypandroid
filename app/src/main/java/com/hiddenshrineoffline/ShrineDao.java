package com.hiddenshrineoffline;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ShrineDao {
    @Query("SELECT * FROM shrineentity")
    List<ShrineEntity> getAll();

    @Query("SELECT * FROM shrineentity WHERE pk IN (:shrineIds)")
    List<ShrineEntity> loadAllByIds(int[] shrineIds);


    @Query("SELECT * FROM shrineentity WHERE shrine_uid LIKE :shrineUid")
    ShrineEntity findByShrineUid(String shrineUid);

    @Insert
    void insertAll(ShrineEntity... shrineEntities);

    @Delete
    void delete(ShrineEntity shrineEntity);
}
