package com.example.projectchat.Signal;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.IdentityKeyStore;

public class identitykeyStore implements IdentityKeyStore {
    IdentityKeyPair identityKeyPair;

    public identitykeyStore(IdentityKeyPair paramIdentityKeyPair) {
        this.identityKeyPair = paramIdentityKeyPair;
    }

    public IdentityKey getIdentity(SignalProtocolAddress paramSignalProtocolAddress) {
        return this.identityKeyPair.getPublicKey();
    }

    public IdentityKeyPair getIdentityKeyPair() {
        return this.identityKeyPair;
    }

    public int getLocalRegistrationId() {
        return 123;
    }

    public boolean isTrustedIdentity(SignalProtocolAddress paramSignalProtocolAddress, IdentityKey paramIdentityKey, IdentityKeyStore.Direction paramDirection) {
        return true;
    }

    public boolean saveIdentity(SignalProtocolAddress paramSignalProtocolAddress, IdentityKey paramIdentityKey) {
        return true;
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Signal\identitykeyStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */