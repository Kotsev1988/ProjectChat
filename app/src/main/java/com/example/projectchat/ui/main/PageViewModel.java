package com.example.projectchat.ui.main;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<String> mIndex = new MutableLiveData<>();
    private MutableLiveData<Bundle> bundleMutableLiveData = new MutableLiveData<Bundle>();
    private MutableLiveData<Bundle> bundleMutableLiveDataUser = new MutableLiveData<Bundle>();

    public void setText(String login) {
        mIndex.setValue(login);
    }

    public MutableLiveData<Bundle> getBundleMutableLiveDataUser() {
        if (bundleMutableLiveDataUser == null)
            bundleMutableLiveDataUser = new MutableLiveData<Bundle>();
        return bundleMutableLiveDataUser;
    }

    public void setBudleUser(Bundle budndle) {
        bundleMutableLiveDataUser.setValue(budndle);
    }

    public MutableLiveData<Bundle> getBundleMutableLiveData() {
        if (bundleMutableLiveData == null)
            bundleMutableLiveData = new MutableLiveData<Bundle>();
        return bundleMutableLiveData;
    }

    public void setBudle(Bundle budle) {
        bundleMutableLiveData.setValue(budle);
    }

    public void setpostBudle(Bundle budle) {
        bundleMutableLiveData.postValue(budle);
    }

    public MutableLiveData<String> getmIndex() {
        if (mIndex == null)
            mIndex = new MutableLiveData<String>();
        return mIndex;
    }
}