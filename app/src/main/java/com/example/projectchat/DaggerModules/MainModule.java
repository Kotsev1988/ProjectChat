package com.example.projectchat.DaggerModules;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {
    Application mapplication;

    public MainModule(Application application) {
        mapplication = application;
    }

    @Provides
    public Application providesApp() {
        return mapplication;
    }

}
