package com.example.projectchat.RoomDB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class message_chattest17 {
    @PrimaryKey(autoGenerate = true)
    public Integer _id;
    public String from1;
    public String to1;
    public String timestampid;
    public String body1;
    public String picture1;
    public String idstanza1;
    public Integer mark;

    public String getBody() {
        return this.body1;
    }

    public String getNameFrom() {
        return this.from1;
    }

    public String getDate() {
        return this.timestampid;
    }

    public String setBitmap() {
        return this.picture1;
    }

    public String getTo() {
        return this.to1;
    }

}
