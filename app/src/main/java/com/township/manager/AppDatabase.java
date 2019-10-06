package com.township.manager;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Notice.class, Wing.class, NoticeWing.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WingDao wingDao();
    public abstract NoticeDao noticeDao();
    public abstract NoticeWingDao noticeWingsDao();
}
