package com.mercury.discovery.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


@Component
public class AESUtils {

    @Value("${apps.encryption.key:moca-base-aes-secret-key}")
    public String encKey;

    private Cipher decryptor;
    private Cipher encryptor;

    private final String AES_PREFIX = "{AES.enc}";

    @PostConstruct
    public void init() {
        try {
            encKey = StringFormatUtils.padRightSpaces(encKey, 32);
            if (encKey.length() > 32) {
                encKey = encKey.substring(0, 32);
            }

            String initialVector = "00000000000000000000000000000000";
            String algorithm = "AES/CBC/PKCS5Padding";
            IvParameterSpec iv = new IvParameterSpec(Hex.decodeHex(initialVector.toCharArray()));

            byte[] keyValue = encKey.getBytes();
            Key key = new SecretKeySpec(keyValue, "AES");

            encryptor = Cipher.getInstance(algorithm);
            encryptor.init(Cipher.ENCRYPT_MODE, key, iv);

            decryptor = Cipher.getInstance(algorithm);
            decryptor.init(Cipher.DECRYPT_MODE, key, iv);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | DecoderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("AESUtils init fail", e);
        }
    }

    public String encrypt(String data) {
        return this.encrypt(data, true);
    }

    public String encrypt(String data, boolean prependPrefix) {
        try {

            if (data.startsWith(AES_PREFIX)) {
                // {AES.enc}으로 시작하면 이미 암호화된 문자로 판단하여 암호화 하지 않음.
                return data;
            }

            byte[] encVal = encryptor.doFinal(data.getBytes());
            return (prependPrefix ? AES_PREFIX : "") + Base64.getEncoder().encodeToString(encVal);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("AESUtils encrypt fail", e);
        }
    }

    public String decrypt(String encryptedData) {
        return this.decrypt(encryptedData, true);
    }

    public String decrypt(String encryptedData, boolean checkPrefix) {
        try {
            if (StringUtils.isEmpty(encryptedData) ||
                    // {AES.enc}으로 시작하지 않으면 복호화 필요없는 문자로 판단
                    (checkPrefix && !encryptedData.startsWith(AES_PREFIX))) {
                return encryptedData;
            }

            if (encryptedData.startsWith(AES_PREFIX)) {
                encryptedData = encryptedData.substring(AES_PREFIX.length());
            }

            byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
            byte[] decValue = decryptor.doFinal(decordedValue);
            return new String(decValue);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("AESUtils decrypt fail", e);
        }
    }
}
