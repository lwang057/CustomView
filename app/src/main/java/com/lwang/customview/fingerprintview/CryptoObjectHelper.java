package com.lwang.customview.fingerprintview;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * @author lwang
 * @date 2018/10/22
 * @description CryptoObject 是一个加密的对象类，用来保证指纹认证的安全性。
 * 采用对称加密
 * 主要实现步骤如下：
 * 1. 新建一个KeyStore密钥库，用于存放密钥
 * 2. 获取KeyGenerator密钥生成工具，生成密钥
 * 3. 通过密钥初始化Cipher对象，生成加密对象CryptoObject
 */
@RequiresApi(Build.VERSION_CODES.M)
public class CryptoObjectHelper {

    static final String KEY_NAME = "com.lwang.customview.fingerprintview.CryptoObjectHelper";
    static final String KEYSTORE_NAME = "AndroidKeyStore";
    static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    static final String TRANSFORMATION = KEY_ALGORITHM + "/" + BLOCK_MODE + "/" + ENCRYPTION_PADDING;
    private KeyStore mKeyStore;

    /**
     * 构造方法，并创建KeyStore密钥库用来存放密钥
     *
     * @throws Exception
     */
    public CryptoObjectHelper() throws Exception {
        mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
        mKeyStore.load(null);
    }

    /**
     * 返回CryptoObject对象，验证指纹时用来加密
     *
     * @return
     * @throws Exception
     */
    public FingerprintManagerCompat.CryptoObject buildCryptoObject() throws Exception {
        Cipher cipher = createCipher(true);
        return new FingerprintManagerCompat.CryptoObject(cipher);
    }

    /**
     * 创建Cipher对象，用来实例化CryptoObject对象
     *
     * @param retry
     * @return
     * @throws Exception
     */
    private Cipher createCipher(boolean retry) throws Exception {
        // 创建并获取密钥key
        CreateKey();
        Key key = mKeyStore.getKey(KEY_NAME, null);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (KeyPermanentlyInvalidatedException e) {
            mKeyStore.deleteEntry(KEY_NAME);
            if (retry) {
                createCipher(false);
            } else {
                throw new Exception("Could not create the cipher for fingerprint authentication.", e);
            }
        }
        return cipher;
    }

    /**
     * 创建KeyGenerator对象 其实就是密钥的生成工具，然后用KeyGenerator来生成key
     *
     * @throws Exception
     */
    private void CreateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(ENCRYPTION_PADDING)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGenerator.init(keyGenSpec);
        keyGenerator.generateKey();
    }

    public void onDestroy() {
        mKeyStore = null;
    }

}