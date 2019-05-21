package com.androidex.util;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES加密工具类
 * Created by yihaibin on 15/10/23.
 */
public class DesUtil {

    /**
     * "mBiw/w8LfV6xml/x6R4rgg==" 07452201897
     * @param encryptString
     * @param encryptKey
     * @return
     * @throws Exception
     */
    public static String encryptDES(String encryptString, String encryptKey) throws Exception {

        SecureRandom sr = new SecureRandom();
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, sr);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    public static String decryptDES(String encryptString, String decryptKey) throws Exception {

        SecureRandom sr = new SecureRandom();
        byte[] byteMi = Base64.decode(encryptString, Base64.DEFAULT);

        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, sr);
        byte[] decryptedData = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }
}
