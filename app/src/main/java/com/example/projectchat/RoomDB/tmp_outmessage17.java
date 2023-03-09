package com.example.projectchat.RoomDB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class tmp_outmessage17 {
    @PrimaryKey
    @NonNull
    public Integer _id;
    public String tmpto;
    public String tmpbody;
    public String idtmpbody;
    public String idstanzatmp;
    public String tmptype;
    public String tmppicture1;

    public String getto() {
        return tmpto;
    }

    public String getTmp_body() {
        return tmpbody;
    }

    public String getTmp_type() {
        return tmptype;
    }

    public String getIdstanzatmp() {
        return idstanzatmp;
    }

    public String getIdtmpbody() {
        return idtmpbody;
    }

    public String getTmppicture1() {
        return tmppicture1;
    }


}