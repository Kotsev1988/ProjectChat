package com.example.projectchat.RoomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {logins.class, message_chattest17.class, message_withtest17.class, tmp_outmessage17.class}, version = 5)
public abstract class DataBase extends RoomDatabase {
    public abstract MyLoginsDao myLoginsDao();

    public abstract mesagechatest_dao mesagechatest_Dao();

    public abstract message_withtestDAO message_withtestDAO();

    public abstract tmp_outmessageDAO tmp_messageoutDAO();
}
