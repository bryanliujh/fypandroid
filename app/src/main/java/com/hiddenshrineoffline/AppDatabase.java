package com.hiddenshrineoffline;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ShrineEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ShrineDao shrineDao();

    private static volatile AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class, "App_Database").allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}