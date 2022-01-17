package com.mercury.discovery.common;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DefaultPasswordEncoder implements PasswordEncoder {

    public static String sha256(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainText.getBytes(StandardCharsets.UTF_8));
            return byteToHexString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("sha256 fail.. NoSuchAlgorithmException", e);
        }
    }

    public static String byteToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return sha256(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }

    public static void main(String[] args) {
        DefaultPasswordEncoder c = new DefaultPasswordEncoder();
        String d = c.encode("1111");
        System.out.println(d);
    }
}
