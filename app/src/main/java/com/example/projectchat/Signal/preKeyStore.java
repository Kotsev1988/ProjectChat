package com.example.projectchat.Signal;

import android.util.Log;

import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.PreKeyStore;

public class preKeyStore implements PreKeyStore {
    PreKeyRecord preKeyRecord;

    public preKeyStore() {
    }

    public preKeyStore(PreKeyRecord paramPreKeyRecord) {
        this.preKeyRecord = paramPreKeyRecord;
    }

    public boolean containsPreKey(int paramInt) {
        return true;
    }

    public PreKeyRecord loadPreKey(int paramInt) throws InvalidKeyIdException {
        return this.preKeyRecord;
    }

    public void removePreKey(int paramInt) {
    }

    public void storePreKey(int paramInt, PreKeyRecord paramPreKeyRecord) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ");
        stringBuilder.append(paramInt);
        stringBuilder.append(" = ");
        stringBuilder.append(paramPreKeyRecord.getKeyPair().getPublicKey().serialize());
        Log.d("PreKeyStore", stringBuilder.toString());
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Signal\preKeyStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */