package com.example.projectchat.RoomDB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface mesagechatest_dao {

    @Query("SELECT * FROM message_chattest17")
    Flowable<List<message_chattest17>> getItemList();

    @Query("SELECT COUNT(*) FROM message_chattest17")
    int getSize();

    @Query("SELECT * FROM message_chattest17 WHERE (from1=:from2 AND to1=:to2) OR (to1=:from2 AND from1=:to2) ORDER BY timestampid DESC")
    List<message_chattest17> getData(String from2, String to2);

    @Query("SELECT * FROM message_chattest17 ORDER BY _id DESC LIMIT 1")
    Flowable<List<message_chattest17>> getDatauser();

    @Query("SELECT * FROM message_chattest17 WHERE to1=:to2 ORDER BY timestampid DESC")
    List<message_chattest17> getDataGroup(String to2);

    @Insert
    void insert(message_chattest17 message_chattest17);

    @Query("SELECT picture1 FROM message_chattest17 WHERE picture1=:body AND idstanza1 =:idstanzamess")
    String isGetPictforGroup(String body, String idstanzamess);

    @Query("SELECT timestampid FROM message_chattest17 WHERE body1=:body AND idstanza1 =:idstanzamess")
    String isReaded(String body, String idstanzamess);

    @Query("SELECT timestampid FROM message_chattest17 ORDER BY timestampid DESC")
    String timestamp();

    @Query("SELECT picture1 FROM message_chattest17 ORDER BY timestampid DESC")
    String timestamppic();

    @Query("DELETE FROM message_chattest17")
    void delete();

}
