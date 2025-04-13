package org.platform.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(charSequence.toString().getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2) {
                    h.insert(0, "0");
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return new MD5Encoder().encode(charSequence).equals(s);
    }
}
