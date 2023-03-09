package com.example.projectchat.Cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherText {
    byte[] bytes = new byte[16];

    Cipher cipher;

    IvParameterSpec iv;

    SecretKey secretKey;

    private SecretKey createSecretKey() {
        try {
            return new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(this.bytes), 16), "AES");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
            return null;
        }
    }

    public Cipher getCipher() {
        return this.cipher;
    }

    public void initCipher() {
        try {
            this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            this.secretKey = createSecretKey();
            (new SecureRandom()).nextBytes(this.bytes);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(this.bytes);
            this.iv = ivParameterSpec;
            this.cipher.init(1, this.secretKey, ivParameterSpec);
            return;
        } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
            invalidAlgorithmParameterException.printStackTrace();
            return;
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
            return;
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {

        } catch (NoSuchPaddingException noSuchPaddingException) {
        }
        //noSuchPaddingException.printStackTrace();
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Cipher\CipherText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */