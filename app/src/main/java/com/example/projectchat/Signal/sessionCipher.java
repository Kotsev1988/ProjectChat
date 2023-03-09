package com.example.projectchat.Signal;

import org.whispersystems.libsignal.SessionCipher;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.state.PreKeyStore;
import org.whispersystems.libsignal.state.SessionStore;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyStore;

public class sessionCipher extends SessionCipher {
    public sessionCipher(SessionStore paramSessionStore, PreKeyStore paramPreKeyStore, SignedPreKeyStore paramSignedPreKeyStore, IdentityKeyStore paramIdentityKeyStore, SignalProtocolAddress paramSignalProtocolAddress) {
        super(paramSessionStore, paramPreKeyStore, paramSignedPreKeyStore, paramIdentityKeyStore, paramSignalProtocolAddress);
    }

    public sessionCipher(SignalProtocolStore paramSignalProtocolStore, SignalProtocolAddress paramSignalProtocolAddress) {
        super(paramSignalProtocolStore, paramSignalProtocolAddress);
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Signal\sessionCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */