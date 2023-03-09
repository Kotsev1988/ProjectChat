package com.example.projectchat.RoomDB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class message_withtest17 {
    @PrimaryKey(autoGenerate = true)
    public Integer _id1;
    public String mwith1;
    public String nick1;
    public String type;
    public String status;
    public String mtime1;
    public Integer mark;
    public String avatar;
    public Integer typing;

}
