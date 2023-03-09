package com.example.projectchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class sqLite {

    ArrayList<Contact_users> cUsers = new ArrayList<Contact_users>();
    ArrayList<UserMessage> messageList = new ArrayList<>();
    ArrayList<outMessage> messageTMP = new ArrayList<>();
    ArrayList<Mucs> mucs = new ArrayList<>();

    int l = 0;
    private static final String DB_NAME = "chatDB22";
    private static final int DB_VERSION = 5;

    private static final String table_name = "message_chattest17";
    public final static String chat_id = "_id";
    public static final String from = "from1";
    public static final String to = "to1";
    public static final String timestampid = "timestampid";
    public static final String body = "body1";
    public static final String picture = "picture1";
    public static final String time1 = "mtime";
    public static final String idbody = "idstanza1";
    public static final String mark = "mark";

    private static final String table_name1 = "message_withtest17";
    public final static String chat_id1 = "_id1";
    public static final String message_with1 = "mwith1";
    public static final String nick = "nick1";
    public static final String type = "type";
    public static final String status = "status";
    public static final String time11 = "mtime1";
    public static final String markusers = "mark";
    public static final String avatar = "avatar";
    public static final String typing =  "typing";


    private static final String table_name2 = "tmp_outmessage17";
    public final static String tmp_id = "_id";
    public static final String tmp_to = "tmpto";
    public static final String tmp_body = "tmpbody";
    public static final String idtmpbody = "idtmpbody";
    public static final String tmp_type = "tmptype";
    public static final String idstanzatmp = "idstanzatmp";

    private static final String my_data = "logins";
    public final static String data_id = "_id";
    public static final String my_user_name = "my_user_name";
    public static final String my_nick = "my_nick";
    public static final String password = "password";

    private static final String Chat_Create_Table111 = "create table " + table_name + "(" + chat_id + " INTEGER primary key AUTOINCREMENT, " + from + " TEXT, " + to + " TEXT, " + timestampid + " TEXT," + body + " TEXT, " + idbody + " TEXT, " + picture + " TEXT, " + mark + " INTEGER" + ");";

    private static final String Message_with111 = "create table " + table_name1 + "(" + chat_id1 + " INTEGER primary key AUTOINCREMENT, " + message_with1 + " TEXT, " + nick + " TEXT, " + type + " TEXT, " +status+" TEXT, "+ markusers + " INTEGER, " + time11 + " TEXT," +avatar+" TEXT,"+typing+" TEXT "+ ");";
    private static final String tmp_chat = "create table " + table_name2 + "(" + tmp_id + " INTEGER primary key AUTOINCREMENT NOT NULL, " + tmp_to + " TEXT, " + tmp_body + " TEXT, " + idtmpbody + " TEXT, " + idstanzatmp + " TEXT, " + tmp_type + " TEXT " + ");";
    private static final String my_login = "create table " + my_data + "(" + data_id + " INTEGER primary key AUTOINCREMENT NOT NULL, " + my_user_name + " TEXT, " + my_nick + " TEXT, " + password+ " TEXT "+");";

    private final Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    String userjid, UserJid, getUserNick, getUserType, getTimestamp, getTimestampgroup, tmpstring, isOuted;
    boolean isReaded;


    public sqLite(Context mContext) {
        this.mContext = mContext;
    }

    public void open() {
        mDBHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        mDB.close();
    }

    public Cursor getChat() {
        return mDB.query(table_name, null, null, null, null, null, null);
    }

    public Cursor getChattmp() {
        return mDB.query(table_name2, null, null, null, null, null, null);
    }

    public void updateDatabase(Map<String, String> s) {
        ContentValues cv = new ContentValues();
        mDBHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        cv.put(from, s.get("from"));
        cv.put(to, s.get("to"));
        cv.put(timestampid, s.get("timestampid"));
        cv.put(body, s.get("message"));
        cv.put(idbody, s.get("idstanza"));
        cv.put(picture, s.get("picture1"));
        cv.put(mark, s.get("mark"));
        mDB.insert(table_name, null, cv);
    }

    public void updateTmpDB(Map<String, String> s) {
        ContentValues cv = new ContentValues();
        mDBHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        cv.put(tmp_to, s.get("tmpto"));
        cv.put(tmp_body, s.get("tmpbody"));
        cv.put(idtmpbody, s.get("idtmpbody"));
        cv.put(tmp_type, s.get("tmptype"));
        cv.put(idstanzatmp, s.get("idstanzatmp"));
        mDB.insert(table_name2, null, cv);
    }

    public void updateMyLogin(Map<String, String> s) {
        ContentValues cv = new ContentValues();
        mDBHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        cv.put(my_user_name, s.get("my_user_name"));
        cv.put(my_nick, s.get("my_nick"));
        cv.put(password, s.get("password"));
        mDB.insert(my_data, null, cv);
    }

    public void updateDatabaseUser(Map<String, String> s) {
        ContentValues cv = new ContentValues();
        mDBHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();

        Cursor c = mDB.query(table_name1, null, null, null, null, null, null);
        c.moveToFirst();
        cv.put(message_with1, s.get("m_with"));
        cv.put(nick, s.get("nick"));
        cv.put(type, s.get("type"));
        cv.put(status, s.get("status"));
        cv.put(markusers, s.get("markusers"));
        cv.put(time11, s.get("time"));
        cv.put(avatar, s.get("avatar"));
        cv.put(typing, s.get("typing"));
        mDB.insert(table_name1, null, cv);
        if (c.moveToFirst()) {
            do {
                int k = c.getColumnIndex(message_with1);
                Log.d("c.getString(k)=", "" + c.getString(k) + "s.get=" + s.get("m_with"));
                if (c.getString(k).equals(s.get("m_with"))) {
                    l++;
                }
            } while (c.moveToNext());
        }
    }

    public ArrayList<UserMessage> getData(String from2, String to2) {

        Cursor c = mDB.query(table_name, null, "(from1=? AND to1=?) OR (to1=? AND from1=?) ", new String[]{from2, to2, from2, to2}, null, null, null);
        c.moveToFirst();

        if (c.moveToFirst()) {
            do {
                int k = c.getColumnIndex(from);
                int k1 = c.getColumnIndex(body);
                int k2 = c.getColumnIndex(to);
                int k3 = c.getColumnIndex(picture);
                int k4 = c.getColumnIndex(time1);
                int k5 = c.getColumnIndex(timestampid);
                Log.d("sqlmessageto", "picture" + c.getString(k5));
                if (c.getString(k).matches(from2)) {
                    if (c.getString(k3) != null) {
                        messageList.add(new UserMessage(null, c.getString(k), c.getString(k5), 4, null, c.getString(k3)));
                    }
                    messageList.add(new UserMessage(c.getString(k1), c.getString(k), c.getString(k5), 1, null, null));
                } else if (c.getString(k).matches(to2)) {
                    if (c.getString(k3) != null) {
                        Log.d("sqlmessageto", "picture" + c.getString(k3));
                        messageList.add(new UserMessage(null, c.getString(k), c.getString(k5), 3, c.getString(k3), null));
                    }
                    messageList.add(new UserMessage(c.getString(k1), c.getString(k), c.getString(k5), 2, null, null));
                }
            } while (c.moveToNext());
        }
        return messageList;
    }


    public ArrayList<UserMessage> getDataGroup(String from2, String to2) {

        Cursor c = mDB.query(table_name, null, "to1=?", new String[]{to2}, null, null, null);
        c.moveToFirst();

        if (c.moveToFirst()) {
            do {
                int k = c.getColumnIndex(from);
                int k1 = c.getColumnIndex(body);
                int k2 = c.getColumnIndex(to);
                int k3 = c.getColumnIndex(picture);
                // int k4=c.getColumnIndex(time1);
                int k5 = c.getColumnIndex(timestampid);
                if (!(c.getString(k).matches(from2))) {
                    if (c.getString(k3) != null) {
                        Log.d("sqlmessageto", "picture" + c.getString(k3));
                        messageList.add(new UserMessage(null, getNick(c.getString(k)), c.getString(k5), 3, c.getString(k3), null));
                    }

                    messageList.add(new UserMessage(c.getString(k1), getNick(c.getString(k)), c.getString(k5), 2, null, null));
                } else if (c.getString(k).matches(from2)) {
                    Log.d("sql from", "" + c.getString(k));
                    Log.d("sql body", "" + c.getString(k1));
                    Log.d("sql picture", "" + c.getString(k3));
                    Log.d("sql timestamp", "" + c.getString(k5));
                    Log.d("sql to", "" + c.getString(k2));
                    if (c.getString(k3) != null) {
                        Log.d("sqlmessageto", "picture" + c.getString(k3));
                        messageList.add(new UserMessage(null, getNick(c.getString(k)), c.getString(k5), 4, null, c.getString(k3)));
                    }
                    messageList.add(new UserMessage(c.getString(k1), getNick(c.getString(k)), c.getString(k5), 1, null, null));
                }

            } while (c.moveToNext());
        }
        return messageList;
    }

    public ArrayList<Contact_users> getDataUser() {

        Cursor c = mDB.query(table_name1, null, null, null, null, null, "mtime1 DESC");
        c.moveToFirst();

        if (c.moveToFirst()) {
            do {
                int k = c.getColumnIndex(message_with1);
                int k1 = c.getColumnIndex(nick);
                int k2 = c.getColumnIndex(time11);
                int k3 = c.getColumnIndex(type);
                int k4 = c.getColumnIndex(status);
                int k5 = c.getColumnIndex(markusers);
                int k6 = c.getColumnIndex(avatar);
                int k7 = c.getColumnIndex(typing);
                Date date = new Date(c.getLong(k2));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                //cUsers.add(new Contact_users(c.getString(k1), c.getString(k4), R.drawable.ic_launcher_background,0, false));
            } while (c.moveToNext());
        }
        return cUsers;
    }

    public String checkUser(String jidUser) {
        String query1 = "select mwith1, nick1 from " + table_name1 + " where mwith1=?";

        Cursor c1 = mDB.rawQuery(query1, new String[]{jidUser}, null);
        if (c1 != null) {
            if (c1.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c1.getColumnNames()) {
                        str = str.concat(cn + " = " + c1.getString(c1.getColumnIndex(cn)) + " ; ");
                    }
                    Log.d("log.d", str);
                    userjid = str;
                } while (c1.moveToNext());
            } else {
                Log.d("log.d", "null");
            }
        }
        Log.d("userjid", "" + userjid);
        return userjid;
    }

    public ArrayList<Mucs> getMucs() {
        Cursor c = mDB.query(table_name1, null, "type=?", new String[]{"group"}, null, null, null);
        c.moveToFirst();

        if (c.moveToFirst()) {
            do {
                int k = c.getColumnIndex(message_with1);

                mucs.add(new Mucs(c.getString(k)));
            } while (c.moveToNext());
        }
        return mucs;
    }

    public String getJid(String userName) {
        String qetjid = "select mwith1 from " + table_name1 + " where nick1=?";
        Cursor cGetjid = mDB.rawQuery(qetjid, new String[]{userName}, null);
        if (cGetjid != null) {
            if (cGetjid.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : cGetjid.getColumnNames()) {
                        str = str.concat(cn + " = " + cGetjid.getString(cGetjid.getColumnIndex(cn)) + " ; ");
                        Log.d("cn", "" + cGetjid.getString(cGetjid.getColumnIndex(cn)));
                        UserJid = cGetjid.getString(cGetjid.getColumnIndex(cn));
                    }
                    Log.d("log.d11", str);
                    //UserJid = str;
                } while (cGetjid.moveToNext());
            }
        }
        return UserJid;
    }


    public String getMyJid() {
        String qetjid = "select my_user_name from " + my_data;
        Cursor cGetjid = mDB.rawQuery(qetjid, null, null);
        if (cGetjid != null) {
            if (cGetjid.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : cGetjid.getColumnNames()) {
                        str = str.concat(cn + " = " + cGetjid.getString(cGetjid.getColumnIndex(cn)) + " ; ");
                        Log.d("cn", "" + cGetjid.getString(cGetjid.getColumnIndex(cn)));
                        UserJid = cGetjid.getString(cGetjid.getColumnIndex(cn));
                    }
                    Log.d("log.d11", str);
                    //UserJid = str;
                } while (cGetjid.moveToNext());
            }
        }
        return UserJid;
    }

    public String getNick(String userNick) {
        String qetnick = "select nick1 from " + table_name1 + " where mwith1=?";
        Cursor cGetjid = mDB.rawQuery(qetnick, new String[]{userNick}, null);
        if (cGetjid != null) {
            if (cGetjid.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : cGetjid.getColumnNames()) {
                        str = str.concat(cn + " = " + cGetjid.getString(cGetjid.getColumnIndex(cn)) + " ; ");
                        Log.d("cn", "" + cGetjid.getString(cGetjid.getColumnIndex(cn)));
                        getUserNick = cGetjid.getString(cGetjid.getColumnIndex(cn));
                    }
                    Log.d("log.d11", str);
                } while (cGetjid.moveToNext());
            }
        }
        return getUserNick;
    }

    public String getMyNick() {
        String qetnick = "select my_nick from " + my_data;
        Cursor cGetjid = mDB.rawQuery(qetnick, null, null);
        if (cGetjid != null) {
            if (cGetjid.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : cGetjid.getColumnNames()) {
                        str = str.concat(cn + " = " + cGetjid.getString(cGetjid.getColumnIndex(cn)) + " ; ");
                        Log.d("cn", "" + cGetjid.getString(cGetjid.getColumnIndex(cn)));
                        getUserNick = cGetjid.getString(cGetjid.getColumnIndex(cn));
                    }
                    Log.d("log.d11", str);
                } while (cGetjid.moveToNext());
            }
        }
        return getUserNick;
    }


    public String getTypeChat(String userNick) {
        String qetnick = "select type from " + table_name1 + " where mwith1=?";
        Cursor cGetjid = mDB.rawQuery(qetnick, new String[]{userNick}, null);
        if (cGetjid != null) {
            if (cGetjid.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : cGetjid.getColumnNames()) {
                        str = str.concat(cn + " = " + cGetjid.getString(cGetjid.getColumnIndex(cn)) + " ; ");
                        Log.d("cn", "" + cGetjid.getString(cGetjid.getColumnIndex(cn)));
                        getUserType = cGetjid.getString(cGetjid.getColumnIndex(cn));
                    }
                    Log.d("log.d11", str);
                } while (cGetjid.moveToNext());
            }
        }
        return getUserType;
    }

    public void updateTime(String time, String who) {
        ContentValues cv = new ContentValues();
        cv.put(time11, time);
        mDB.update(table_name1, cv, "mwith1=?", new String[]{who});
    }

    public void updateMark(String mark, String who) {
        ContentValues cv = new ContentValues();
        cv.put(markusers, mark);
        mDB.update(table_name1, cv, "mwithtimestamppic()1=?", new String[]{who});
    }

    public boolean isExist() {
        String isExist = "SELECT name FROM sqlite_master WHERE type=? AND name=?";
        Cursor cisexist = mDB.query(my_data, null, null, null, null, null, null);
        if (cisexist.getCount() > 0) {
            Log.d("isExist", "" + cisexist.toString());
            Log.d("isExist", "" + cisexist.getCount());
            return true;
        } else {
            Log.d("isExist", "null");
            return false;
        }
    }

    public String timestamp() {
        String timest = "SELECT timestampid FROM message_chattest17";
        Cursor cisexist = mDB.rawQuery(timest, null, null);
        if (cisexist.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : cisexist.getColumnNames()) {
                    str = str.concat(cn + " = " + cisexist.getString(cisexist.getColumnIndex(cn)) + " ; ");
                    getTimestamp = cisexist.getString(cisexist.getColumnIndex(cn));
                }
            } while (cisexist.moveToNext());
        }
        return getTimestamp;
    }

    public String timestampgroup() {
        String timest1 = "SELECT body1, timestampid FROM message_chattest17 WHERE mark=?";
        Cursor cisexist1 = mDB.rawQuery(timest1, new String[]{"110"}, null);

        if (cisexist1.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : cisexist1.getColumnNames()) {
                    str = str.concat(cn + " = " + cisexist1.getString(cisexist1.getColumnIndex(cn)) + " ; ");
                    Log.d("cn", "" + cisexist1.getString(cisexist1.getColumnIndex(cn)));
                    getTimestampgroup = cisexist1.getString(cisexist1.getColumnIndex(cn));
                }
            } while (cisexist1.moveToNext());
        }
        return getTimestampgroup;
    }

    public boolean isReaded(String body, String idstanzamess) {
        String timest = "SELECT body1 FROM " + table_name + " WHERE body1=? AND idstanza1=?";
        Cursor cisexist = mDB.rawQuery(timest, new String[]{body, idstanzamess}, null);
        isReaded = false;
        if (cisexist.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : cisexist.getColumnNames()) {
                    str = str.concat(cn + " = " + cisexist.getString(cisexist.getColumnIndex(cn)) + " ; ");
                    Log.d("cn", "" + cisexist.getString(cisexist.getColumnIndex(cn)));
                    if (!cisexist.getString(cisexist.getColumnIndex(cn)).isEmpty()) {
                        isReaded = true;
                    }
                }
            } while (cisexist.moveToNext());
        }
        return isReaded;
    }

    public boolean isGetPictforGroup(String body, String idstanzamess) {
        String timest = "SELECT picture1 FROM " + table_name + " WHERE picture1=? AND idstanza1=?";
        Cursor cisexist = mDB.rawQuery(timest, new String[]{body, idstanzamess}, null);
        isReaded = false;

        if (cisexist.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : cisexist.getColumnNames()) {
                    str = str.concat(cn + " = " + cisexist.getString(cisexist.getColumnIndex(cn)) + " ; ");
                    Log.d("cn", "" + cisexist.getString(cisexist.getColumnIndex(cn)));
                    if (!cisexist.getString(cisexist.getColumnIndex(cn)).isEmpty()) {
                        isReaded = true;
                    }
                }
            } while (cisexist.moveToNext());
        }
        return isReaded;
    }

    public String tmp() {
        String timest = "SELECT * FROM tmp_outmessage17 ";
        Cursor cisexist = mDB.rawQuery(timest, null, null);

        if (cisexist.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : cisexist.getColumnNames()) {
                    str = str.concat(cn + " = " + cisexist.getString(cisexist.getColumnIndex(cn)) + " ; ");
                    Log.d("cn", "" + cisexist.getString(cisexist.getColumnIndex(cn)));
                    tmpstring = cisexist.getString(cisexist.getColumnIndex(cn));
                }
            } while (cisexist.moveToNext());
        }
        return tmpstring;
    }


    public ArrayList<outMessage> getTmpMessages() {
        Cursor c = mDB.query(table_name2, null, null, null, null, null, null);
        c.moveToFirst();

        if (c.moveToFirst()) {
            do {
                int k = c.getColumnIndex(tmp_to);
                int k1 = c.getColumnIndex(tmp_body);
                int k4 = c.getColumnIndex(idtmpbody);
                int k2 = c.getColumnIndex(tmp_type);
                int k3 = c.getColumnIndex(idstanzatmp);
                messageTMP.add(new outMessage(c.getString(k), c.getString(k1), c.getString(k4), c.getString(k2), c.getString(k3)));
            } while (c.moveToNext());
        }
        return messageTMP;
    }

    public void delete() {
        mDB.delete(table_name, null, null);
        mDB.delete(table_name1, null, null);
        mDB.delete(table_name2, null, null);
    }

    public void deleteFromTmp(String _idtmpbody) {
        mDB.delete(table_name2, idtmpbody + "=?", new String[]{_idtmpbody});
    }

    public void clearTMP() {
        mDB.delete(table_name2, null, null);
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Chat_Create_Table111);
            Log.d("onCreate", "onCreateDB");
            db.execSQL(Message_with111);
            Log.d("onCreate", "onCreateDBdbSms");
            db.execSQL(tmp_chat);
            Log.d("onCreate", "TmpChat");
            db.execSQL(my_login);
            Log.d("onCreate", "my_login");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}



