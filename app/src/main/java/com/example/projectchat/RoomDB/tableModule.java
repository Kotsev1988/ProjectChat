package com.example.projectchat.RoomDB;

import android.app.Application;
import android.content.Context;
import android.net.sip.SipManager;

import androidx.room.Room;

import com.example.projectchat.UserAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class tableModule {
    public DataBase database;


    public tableModule(Application application) {
        database = Room.databaseBuilder(application.getApplicationContext(), DataBase.class, "chatDB22").build();
    }

    @Provides
    @Singleton
    public DataBase prividesdb() {
        return database;
    }


    @Provides
    @Singleton
    logins providelogins() {
        return new logins();
    }

    @Provides
    @Singleton
    message_chattest17 provideMessageTest_17() {
        return new message_chattest17();
    }

    @Provides
    @Singleton
    message_withtest17 provideMessagewithTest() {
        return new message_withtest17();
    }

    @Provides
    @Singleton
    tmp_outmessage17 provide_tmpmessageout() {
        return new tmp_outmessage17();
    }

    @Provides
    @Singleton
    UserAuth userAuth() {
        return new UserAuth(database);
    }




}
