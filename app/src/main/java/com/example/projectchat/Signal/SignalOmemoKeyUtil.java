package com.example.projectchat.Signal;

import org.jivesoftware.smackx.omemo.element.OmemoBundleElement;
import org.jivesoftware.smackx.omemo.exceptions.CorruptedOmemoKeyException;
import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;
import org.jivesoftware.smackx.omemo.util.OmemoKeyUtil;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECPublicKey;
import org.whispersystems.libsignal.state.PreKeyBundle;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

public class SignalOmemoKeyUtil extends OmemoKeyUtil<IdentityKeyPair, IdentityKey, PreKeyRecord, SignedPreKeyRecord, SessionRecord, ECPublicKey, PreKeyBundle> {
    public PreKeyBundle bundleFromOmemoBundle(OmemoBundleElement paramOmemoBundleElement, OmemoDevice paramOmemoDevice, int paramInt) throws CorruptedOmemoKeyException {
        return new PreKeyBundle(0, paramOmemoDevice.getDeviceId(), paramInt, (ECPublicKey) this.BUNDLE.preKeyPublic(paramOmemoBundleElement, paramInt), this.BUNDLE.signedPreKeyId(paramOmemoBundleElement), (ECPublicKey) this.BUNDLE.signedPreKeyPublic(paramOmemoBundleElement), this.BUNDLE.signedPreKeySignature(paramOmemoBundleElement), (IdentityKey) this.BUNDLE.identityKey(paramOmemoBundleElement));
    }

    public ECPublicKey ellipticCurvePublicKeyFromBytes(byte[] paramArrayOfbyte) throws CorruptedOmemoKeyException {
        if (paramArrayOfbyte == null)
            return null;
        try {
            return Curve.decodePoint(paramArrayOfbyte, 0);
        } catch (InvalidKeyException invalidKeyException) {
            throw new CorruptedOmemoKeyException(invalidKeyException);
        }
    }

    public IdentityKeyPair generateOmemoIdentityKeyPair() {
        return KeyHelper.generateIdentityKeyPair();
    }

    @Override
    public TreeMap<Integer, PreKeyRecord> generateOmemoPreKeys(int currentPreKeyId, int count) {
        List<PreKeyRecord> preKeyRecords = KeyHelper.generatePreKeys(currentPreKeyId, count);
        TreeMap<Integer, PreKeyRecord> map = new TreeMap<>();
        for (PreKeyRecord p : preKeyRecords) {
            map.put(p.getId(), p);
        }
        return map;
    }

    public SignedPreKeyRecord generateOmemoSignedPreKey(IdentityKeyPair paramIdentityKeyPair, int paramInt) throws CorruptedOmemoKeyException {
        try {
            return KeyHelper.generateSignedPreKey(paramIdentityKeyPair, paramInt);
        } catch (InvalidKeyException invalidKeyException) {
            throw new CorruptedOmemoKeyException(invalidKeyException);
        }
    }

    public OmemoFingerprint getFingerprintOfIdentityKey(IdentityKey paramIdentityKey) {
        return (paramIdentityKey == null) ? null : new OmemoFingerprint(paramIdentityKey.getFingerprint().replace("(byte)0x", "").replace(",", "").replace(" ", "").substring(2));
    }

    public OmemoFingerprint getFingerprintOfIdentityKeyPair(IdentityKeyPair paramIdentityKeyPair) {
        return (paramIdentityKeyPair == null) ? null : getFingerprintOfIdentityKey(paramIdentityKeyPair.getPublicKey());
    }

    public byte[] identityKeyForBundle(IdentityKey paramIdentityKey) {
        return paramIdentityKey.getPublicKey().serialize();
    }

    public IdentityKey identityKeyFromBytes(byte[] paramArrayOfbyte) throws CorruptedOmemoKeyException {
        if (paramArrayOfbyte == null)
            return null;
        try {
            return new IdentityKey(paramArrayOfbyte, 0);
        } catch (InvalidKeyException invalidKeyException) {
            throw new CorruptedOmemoKeyException(invalidKeyException);
        }
    }

    public IdentityKey identityKeyFromPair(IdentityKeyPair paramIdentityKeyPair) {
        return paramIdentityKeyPair.getPublicKey();
    }

    public IdentityKeyPair identityKeyPairFromBytes(byte[] paramArrayOfbyte) throws CorruptedOmemoKeyException {
        if (paramArrayOfbyte == null)
            return null;
        try {
            return new IdentityKeyPair(paramArrayOfbyte);
        } catch (InvalidKeyException invalidKeyException) {
            throw new CorruptedOmemoKeyException(invalidKeyException);
        }
    }

    public byte[] identityKeyPairToBytes(IdentityKeyPair paramIdentityKeyPair) {
        return paramIdentityKeyPair.serialize();
    }

    public byte[] identityKeyToBytes(IdentityKey paramIdentityKey) {
        return paramIdentityKey.serialize();
    }

    public byte[] preKeyForBundle(PreKeyRecord paramPreKeyRecord) {
        return paramPreKeyRecord.getKeyPair().getPublicKey().serialize();
    }

    public PreKeyRecord preKeyFromBytes(byte[] paramArrayOfbyte) throws IOException {
        return (paramArrayOfbyte == null) ? null : new PreKeyRecord(paramArrayOfbyte);
    }

    public byte[] preKeyPublicKeyForBundle(ECPublicKey paramECPublicKey) {
        return paramECPublicKey.serialize();
    }

    public byte[] preKeyToBytes(PreKeyRecord paramPreKeyRecord) {
        return paramPreKeyRecord.serialize();
    }

    public SessionRecord rawSessionFromBytes(byte[] paramArrayOfbyte) throws IOException {
        return (paramArrayOfbyte == null) ? null : new SessionRecord(paramArrayOfbyte);
    }

    public byte[] rawSessionToBytes(SessionRecord paramSessionRecord) {
        return paramSessionRecord.serialize();
    }

    public SignedPreKeyRecord signedPreKeyFromBytes(byte[] paramArrayOfbyte) throws IOException {
        return (paramArrayOfbyte == null) ? null : new SignedPreKeyRecord(paramArrayOfbyte);
    }

    public int signedPreKeyIdFromKey(SignedPreKeyRecord paramSignedPreKeyRecord) {
        return paramSignedPreKeyRecord.getId();
    }

    public byte[] signedPreKeyPublicForBundle(SignedPreKeyRecord paramSignedPreKeyRecord) {
        return paramSignedPreKeyRecord.getKeyPair().getPublicKey().serialize();
    }

    public byte[] signedPreKeySignatureFromKey(SignedPreKeyRecord paramSignedPreKeyRecord) {
        return paramSignedPreKeyRecord.getSignature();
    }

    public byte[] signedPreKeyToBytes(SignedPreKeyRecord paramSignedPreKeyRecord) {
        return paramSignedPreKeyRecord.serialize();
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Signal\SignalOmemoKeyUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */