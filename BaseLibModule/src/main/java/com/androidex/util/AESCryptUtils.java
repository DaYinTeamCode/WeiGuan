package com.androidex.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 使用方法：
 * <p>
 * 已有：
 * 密码key(String)：String pwd = "xxxx"
 * 待加密字符串: String originalStr = "yyy"
 * <p>
 * 加密：String encryptStr = AESCryptUtils.encrypt(pwd, originalStr);
 * 解密：String originalStr = AESCryptUtils.decrypt(pwd, encryptStr);
 */
public final class AESCryptUtils {

    private static final String AES_MODE = "AES/CBC/PKCS7Padding";
    private static final String CHARSET = "UTF-8";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String ALGORITHM = "AES";

    private static final byte[] ivBytes = new byte[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    /**
     * 加密
     * 秘钥不做 sha-256摘要
     *
     * @param password
     * @param message
     * @return
     * @throws GeneralSecurityException
     */
    public static String encrypt(String password, String message) throws GeneralSecurityException {

        return encrypt(password, message, false);
    }

    /**
     * 加密
     *
     * @param password
     * @param message
     * @param keyNeedSha256 秘钥是否做sha256摘要算法
     * @return
     * @throws GeneralSecurityException
     */
    public static String encrypt(String password, String message, boolean keyNeedSha256) throws GeneralSecurityException {

        try {

            SecretKeySpec key = generateKey(password, keyNeedSha256);
            byte[] cipherText = encrypt(key, ivBytes, message.getBytes(CHARSET));
            return Base64.encodeToString(cipherText, Base64.NO_WRAP);

        } catch (UnsupportedEncodingException var5) {
            throw new GeneralSecurityException(var5);
        }
    }

    /**
     * 加密
     *
     * @param key
     * @param iv
     * @param message
     * @return
     * @throws GeneralSecurityException
     */
    public static byte[] encrypt(SecretKeySpec key, byte[] iv, byte[] message) throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return cipher.doFinal(message);
    }

    /**
     * 解密
     * 秘钥不做 sha-256摘要
     *
     * @param password
     * @param base64Message
     * @return
     * @throws GeneralSecurityException
     */
    public static String decrypt(String password, String base64Message) throws GeneralSecurityException {

        return decrypt(password, base64Message, false);
    }

    /**
     * 解密
     *
     * @param password
     * @param base64Msg
     * @param keyNeedSha256           秘钥是否做sha256摘要算法
     * @return
     * @throws GeneralSecurityException
     */
    public static String decrypt(String password, String base64Msg, boolean keyNeedSha256) throws GeneralSecurityException {

        try {

            SecretKeySpec key = generateKey(password, keyNeedSha256);
            byte[] decodedCipherText = Base64.decode(base64Msg, 2);
            byte[] decryptedBytes = decrypt(key, ivBytes, decodedCipherText);
            return new String(decryptedBytes, CHARSET);

        } catch (UnsupportedEncodingException var6) {
            throw new GeneralSecurityException(var6);
        }
    }

    /**
     * 解密
     *
     * @param key
     * @param iv
     * @param msgBytes
     * @return
     * @throws GeneralSecurityException
     */
    public static byte[] decrypt(SecretKeySpec key, byte[] iv, byte[] msgBytes) throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(msgBytes);
        return decryptedBytes;
    }

    /**
     * 获取秘钥
     *
     * @param password
     * @param keyNeedSha256 秘钥是否做sha256摘要算法
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static SecretKeySpec generateKey(String password, boolean keyNeedSha256) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        byte[] keyBytes = password.getBytes(CHARSET);
        if (keyNeedSha256) {

            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(keyBytes, 0, keyBytes.length);
            keyBytes = digest.digest();
        }

        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
