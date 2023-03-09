package com.example.projectchat.RoomDB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class logins {
    @PrimaryKey(autoGenerate = true)
    public int _id;
    public String my_user_name;
    public String my_nick;
    public String password;

    public void setId(int id) {
        this._id = id;
    }

    public void set_Name(String name) {
        this.my_user_name = name;
    }

    public void setMyNick(String nick) {
        this.my_nick = nick;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getMy_user_name() {
        return my_user_name;
    }

    public String getMy_nick() {
        return my_nick;
    }

}
