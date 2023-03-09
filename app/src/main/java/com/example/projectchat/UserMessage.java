package com.example.projectchat;

public class UserMessage {

    private String MessageText;
    private int typemessage;
    private String fromName;
    private String datetime;
    private String bmpath;
    private String bm_out_path;

    public UserMessage(String MessageText, String fromName, String _datetime, int typemessage, String _bmpath, String bm_out_path) {
        this.MessageText = MessageText;
        this.fromName = fromName;
        this.typemessage = typemessage;
        this.bmpath = _bmpath;
        this.bm_out_path = bm_out_path;
        this.datetime = _datetime;
    }

    public String getName() {
        return this.MessageText;
    }

    public String getNameFrom() {
        return this.fromName;
    }

    public String getDate() {
        return this.datetime;
    }

    public int messageFrom() {
        return this.typemessage;
    }

    public String setBitmap() {
        return this.bmpath;
    }

    public String setouBitmap() {
        return this.bm_out_path;
    }
}
