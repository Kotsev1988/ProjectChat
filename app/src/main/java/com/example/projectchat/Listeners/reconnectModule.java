package com.example.projectchat.Listeners;

import com.example.projectchat.UserAuth;

import dagger.Module;
import dagger.Provides;

@Module
public class reconnectModule {
    @Provides
    reconnectListener _reconnectListener(UserAuth userAuth) {
        return new reconnectListener(userAuth.getS());
    }
}
