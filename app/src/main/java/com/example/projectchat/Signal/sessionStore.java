package com.example.projectchat.Signal;

import android.util.Log;

import org.jivesoftware.smack.util.stringencoder.Base64;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SessionStore;

import java.util.List;

public class sessionStore implements SessionStore {
    SessionRecord sessionRecord;

    public sessionStore(SessionRecord paramSessionRecord) {
        this.sessionRecord = paramSessionRecord;
    }

    public boolean containsSession(SignalProtocolAddress paramSignalProtocolAddress) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(paramSignalProtocolAddress.getDeviceId());
        Log.d("SessionStore", stringBuilder.toString());
        return false;
    }

    public void deleteAllSessions(String paramString) {
    }

    public void deleteSession(SignalProtocolAddress paramSignalProtocolAddress) {
    }

    public List<Integer> getSubDeviceSessions(String paramString) {
        return null;
    }

    public SessionRecord loadSession(SignalProtocolAddress paramSignalProtocolAddress) {
        return this.sessionRecord;
    }

    public void storeSession(SignalProtocolAddress paramSignalProtocolAddress, SessionRecord paramSessionRecord) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(Base64.encodeToString(paramSessionRecord.getSessionState().getLocalIdentityKey().getPublicKey().serialize()));
        Log.d("identityKeyLocalSt", stringBuilder.toString());
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Signal\sessionStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */