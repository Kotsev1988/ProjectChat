package com.example.projectchat.Signal;

import android.util.Log;

import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyStore;

import java.util.List;

public class signedprekeyStore implements SignedPreKeyStore {
    SignedPreKeyRecord signedPreKeyRecord;

    public signedprekeyStore(SignedPreKeyRecord paramSignedPreKeyRecord) {
        this.signedPreKeyRecord = paramSignedPreKeyRecord;
    }

    public boolean containsSignedPreKey(int paramInt) {
        return true;
    }

    public SignedPreKeyRecord loadSignedPreKey(int paramInt) throws InvalidKeyIdException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SignedPreKeyStore");
        stringBuilder.append(paramInt);
        Log.d("SignedPreKeyStore", stringBuilder.toString());
        return this.signedPreKeyRecord;
    }

    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        return null;
    }

    public void removeSignedPreKey(int paramInt) {
    }

    public int signedPreKeyId() {
        return this.signedPreKeyRecord.getId();
    }

    public byte[] signedSig() {
        return this.signedPreKeyRecord.getSignature();
    }

    public void storeSignedPreKey(int paramInt, SignedPreKeyRecord paramSignedPreKeyRecord) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SignedPreKeyStore");
        stringBuilder.append(paramInt);
        stringBuilder.append("=");
        stringBuilder.append(paramSignedPreKeyRecord.serialize());
        Log.d("SignedPreKeyStore", stringBuilder.toString());
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Signal\signedprekeyStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */