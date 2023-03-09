package com.example.projectchat.DaggerModules;

import android.app.Application;

import com.example.projectchat.Listeners.IncomingListenerChat;
import com.example.projectchat.Listeners.IncomingModule;
import com.example.projectchat.Listeners.reconnectListener;
import com.example.projectchat.Listeners.reconnectModule;
import com.example.projectchat.RoomDB.DataBase;
import com.example.projectchat.RoomDB.logins;
import com.example.projectchat.RoomDB.message_chattest17;
import com.example.projectchat.RoomDB.message_withtest17;
import com.example.projectchat.RoomDB.tableModule;
import com.example.projectchat.RoomDB.tmp_outmessage17;
import com.example.projectchat.UserAuth;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {tableModule.class, MainModule.class, IncomingModule.class, reconnectModule.class})

public interface AppComponent {

    Application application();

    logins logins();

    DataBase database();

    message_chattest17 messagechattest17();

    message_withtest17 message_withtest17();

    tmp_outmessage17 tmp_outmessage17();

    UserAuth userauth();



    IncomingListenerChat incominListenerChat();

    reconnectListener reconnectListener();
}
