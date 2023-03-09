package com.example.projectchat.Signal;

import org.whispersystems.libsignal.SessionBuilder;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.state.PreKeyStore;
import org.whispersystems.libsignal.state.SessionStore;
import org.whispersystems.libsignal.state.SignedPreKeyStore;

public class sessionBulder extends SessionBuilder {
    public sessionBulder(SessionStore paramSessionStore, PreKeyStore paramPreKeyStore, SignedPreKeyStore paramSignedPreKeyStore, IdentityKeyStore paramIdentityKeyStore, SignalProtocolAddress paramSignalProtocolAddress) {
        super(paramSessionStore, paramPreKeyStore, paramSignedPreKeyStore, paramIdentityKeyStore, paramSignalProtocolAddress);
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Signal\sessionBulder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */