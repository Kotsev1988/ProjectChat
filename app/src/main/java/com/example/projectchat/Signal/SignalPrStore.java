package com.example.projectchat.Signal;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.util.List;

public class SignalPrStore implements SignalProtocolStore {
    SessionRecord sessionRecord;

    public SignalPrStore(SessionRecord paramSessionRecord) {
        this.sessionRecord = paramSessionRecord;
    }

    public boolean containsPreKey(int paramInt) {
        return true;
    }

    public boolean containsSession(SignalProtocolAddress paramSignalProtocolAddress) {
        return true;
    }

    public boolean containsSignedPreKey(int paramInt) {
        return false;
    }

    public void deleteAllSessions(String paramString) {
    }

    public void deleteSession(SignalProtocolAddress paramSignalProtocolAddress) {
    }

    public IdentityKey getIdentity(SignalProtocolAddress paramSignalProtocolAddress) {
        return null;
    }

    public IdentityKeyPair getIdentityKeyPair() {
        return null;
    }

    public int getLocalRegistrationId() {
        return 0;
    }

    public List<Integer> getSubDeviceSessions(String paramString) {
        return null;
    }

    public boolean isTrustedIdentity(SignalProtocolAddress paramSignalProtocolAddress, IdentityKey paramIdentityKey, IdentityKeyStore.Direction paramDirection) {
        return true;
    }

    public PreKeyRecord loadPreKey(int paramInt) throws InvalidKeyIdException {
        return null;
    }

    public SessionRecord loadSession(SignalProtocolAddress paramSignalProtocolAddress) {
        return this.sessionRecord;
    }

    public SignedPreKeyRecord loadSignedPreKey(int paramInt) throws InvalidKeyIdException {
        return null;
    }

    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        return null;
    }

    public void removePreKey(int paramInt) {
    }

    public void removeSignedPreKey(int paramInt) {
    }

    public boolean saveIdentity(SignalProtocolAddress paramSignalProtocolAddress, IdentityKey paramIdentityKey) {
        return true;
    }

    public void storePreKey(int paramInt, PreKeyRecord paramPreKeyRecord) {
    }

    public void storeSession(SignalProtocolAddress paramSignalProtocolAddress, SessionRecord paramSessionRecord) {
    }

    public void storeSignedPreKey(int paramInt, SignedPreKeyRecord paramSignedPreKeyRecord) {
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Signal\SignalPrStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */