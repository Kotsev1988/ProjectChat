package com.example.projectchat.RoomDB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface MyLoginsDao {

    @Query("SELECT * FROM logins")
    List<logins> getAll();

    @Query("SELECT * FROM logins")
    Flowable<logins> getAll1();

    @Query("SELECT my_nick from logins")
    String my_nick();

    @Query("SELECT my_user_name from logins")
    String my_user_name();

    @Query("SELECT password from logins")
    String my_pass();

    @Insert
    void insert(logins users);

}
