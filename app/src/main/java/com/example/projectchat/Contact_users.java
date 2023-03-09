package com.example.projectchat;

public class Contact_users {
    String nick;
    String mark;
    int image;
    int flag;
    boolean tag;

    Contact_users(String _nick, String _mark, int _image, int _flag, boolean tag) {
        this.nick = _nick;
        this.mark = _mark;
        this.image = _image;
        this.flag = _flag;
        this.tag = tag;
    }

    public String getnick() {
        return nick;
    }
}
