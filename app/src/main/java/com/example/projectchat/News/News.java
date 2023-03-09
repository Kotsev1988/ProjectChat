package com.example.projectchat.News;

public class News {
    private String MessageText;
    private int typemessage;
    private String fromName;
    private String datetime;
    private String bmpath;

    public News(String _MessageText, int _typemessage, String _fromName, String _datetime, String _bmpath) {
        this.MessageText = _MessageText;
        this.typemessage = _typemessage;
        this.fromName = _fromName;
        this.datetime = _datetime;
        this.bmpath = _bmpath;
    }

    public String getMessageText() {
        return this.MessageText;
    }

    public String getNameFrom() {
        return this.fromName;
    }

    public String getDate() {
        return this.datetime;
    }

    public int getTypemessage() {
        return this.typemessage;
    }

    public String getBmpath() {
        return this.bmpath;
    }

}
