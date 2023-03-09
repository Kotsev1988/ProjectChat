package com.example.projectchat.RoomDB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface message_withtestDAO {
    @Query("SELECT * FROM message_withtest17")
    List<message_withtest17> getAll();

    @Query("SELECT * FROM message_withtest17 ORDER BY mtime1 DESC")
    Flowable<List<message_withtest17>> getAll1();


    @Query("SELECT * FROM message_withtest17 WHERE mwith1=:user ORDER BY mtime1 DESC")
    Flowable<message_withtest17> getAllUser(String user);


    @Query("SELECT nick1 FROM message_withtest17")
    LiveData<String> getnickLiveData();

    @Query("Select nick1 from message_withtest17 where mwith1=:jid")
    String getNick(String jid);

    @Query("Select mark from message_withtest17 where mwith1=:jid")
    int getMark(String jid);

    @Query("UPDATE message_withtest17 SET mark=:mark WHERE mwith1=:mwith1")
    void updateMark(int mark, String mwith1);

    @Query("UPDATE message_withtest17 SET status=:status WHERE mwith1=:mwith1")
    void updateStatus(String status, String mwith1);

    @Query("UPDATE message_withtest17 SET typing=:typing WHERE mwith1=:mwith1")
    void updateTyping(Integer typing, String mwith1);

    @Query("UPDATE message_withtest17 SET avatar=:avatar WHERE mwith1=:mwith1")
    void updateAvatar(String avatar, String mwith1);

    @Query("UPDATE message_withtest17 SET mtime1=:mtime1 WHERE mwith1=:mwith1")
    void updateDBUser(String mtime1, String mwith1);

    @Query("select mwith1 from message_withtest17 where mwith1=:mwith")
    String checkUser(String mwith);

    @Query("SELECT mwith1 FROM message_withtest17 WHERE type='group'")
    List<message_withtest17> getMucs();

    @Query("SELECT type FROM message_withtest17 WHERE mwith1=:userjid")
    String getTypeChat(String userjid);

    @Query("select mwith1 from message_withtest17 where nick1=:nickname")
    String getJid(String nickname);

    @Insert
    void insert(message_withtest17 message_withtest_17);

    @Query("DELETE FROM message_withtest17")
    void delete();
}
