package com.example.projectchat;

public class NewsPerson {

    private String mName;
    private String mText;
    private String mImageUrl;
    private int typecontent;

    public NewsPerson(String name, String text, String imageUrl, int _typecontent) {
        mName = name;
        mText=text;
        mImageUrl = imageUrl;
        typecontent=_typecontent;
    }

    public String getmName() {
        return mName;
    }
    public String getmText() {
        return mText;
    }

    public void setName(String name) {
        mName = name;
    }
    public void setText(String text) {
        mText = text;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public int getTypecontent() {
        return typecontent;
    }

    public void setmImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
