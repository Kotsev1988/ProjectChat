package com.example.projectchat.RoomDB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface tmp_outmessageDAO {
    @Query("SELECT * FROM tmp_outmessage17")
    List<tmp_outmessage17> getAll();

    @Query("DELETE FROM tmp_outmessage17 WHERE idtmpbody=:idtmpbody")
    void deleteFromTmp(String idtmpbody);

    @Insert
    void insert(tmp_outmessage17 tmpOutmessage17);

    @Query("SELECT * FROM tmp_outmessage17")
    Flowable<List<tmp_outmessage17>> getAll1();

    @Query("DELETE FROM tmp_outmessage17")
    void delete();

}
