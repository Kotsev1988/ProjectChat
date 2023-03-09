package com.example.projectchat;

import android.app.Application;
import com.example.projectchat.DaggerModules.AppComponent;
import com.example.projectchat.DaggerModules.DaggerAppComponent;
import com.example.projectchat.DaggerModules.MainModule;
import com.example.projectchat.Listeners.IncomingModule;
import com.example.projectchat.Listeners.reconnectModule;
import com.example.projectchat.RoomDB.tableModule;


public class App extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .mainModule(new MainModule(this))
                .tableModule(new tableModule(this)).incomingModule(new IncomingModule()).reconnectModule(new reconnectModule())
                .build();
    }

    public static AppComponent getComponent() {
        return appComponent;
    }

}