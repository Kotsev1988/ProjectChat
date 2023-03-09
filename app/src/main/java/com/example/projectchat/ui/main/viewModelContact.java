package com.example.projectchat.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class viewModelContact extends ViewModel {
    private MutableLiveData<String> nick = new MutableLiveData<String>();

    public void setNick(String s) {

        nick.postValue(s);
    }

    public LiveData<String> getNick() {
        if (nick == null) {
            nick = new MutableLiveData<String>();
        }
        return nick;
    }


}
