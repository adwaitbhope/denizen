package com.township.manager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Notice.Comment... comments);

    @Delete
    void delete(Notice.Comment comment);

    @Query("DELETE FROM Comment")
    void deleteAll();

}
