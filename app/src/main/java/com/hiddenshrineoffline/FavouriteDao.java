package com.hiddenshrineoffline;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavouriteDao {
    @Query("SELECT * FROM favouriteentity")
    List<FavouriteEntity> getAll();

    @Query("SELECT * FROM favouriteentity WHERE pk IN (:shrineIds)")
    List<FavouriteEntity> loadAllByIds(int[] shrineIds);


    @Query("SELECT * FROM favouriteentity WHERE shrine_uid LIKE :shrineUid")
    FavouriteEntity findByShrineUid(String shrineUid);

    @Insert
    void insertAll(FavouriteEntity... favouriteEntities);

    @Delete
    void delete(FavouriteEntity favouriteEntity);
}
