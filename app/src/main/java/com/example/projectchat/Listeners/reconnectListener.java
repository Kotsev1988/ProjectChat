package com.example.projectchat.Listeners;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;

public class reconnectListener {
    ReconnectionManager reconnectionManager;

    public reconnectListener(AbstractXMPPConnection abstractXMPPConnection) {
        reconnectionManager = ReconnectionManager.getInstanceFor(abstractXMPPConnection);
        reconnectionManager.setFixedDelay(10);
        reconnectionManager.enableAutomaticReconnection();
        reconnectionManager.addReconnectionListener(new ReconnectionListener() {
            @Override
            public void reconnectingIn(int seconds) {
                Log.d("reconnectingIn", "" + seconds);
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.d("reconnectionFailed", "" + e.getMessage().toString());
            }
        });
    }

}
