package com.township.manager;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Notice.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoticeDao noticeDao();
}
