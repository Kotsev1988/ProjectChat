package com.example.projectchat.Listeners;

import com.example.projectchat.UserAuth;

import dagger.Module;
import dagger.Provides;

@Module
public class IncomingModule {

    @Provides
    IncomingListenerChat incomingListenerChat(UserAuth userAuth) {
        return new IncomingListenerChat(userAuth.getS());
    }
}
