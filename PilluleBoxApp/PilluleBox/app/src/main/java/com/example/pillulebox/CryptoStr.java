package com.example.pillulebox;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoStr {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY_ALGORITHM = "AES";
    private static final byte[] SECRET_KEY_BYTES = "yourSecretKeyBytes".getBytes(); // Reemplaza con tus bytes de clave secreta

    public static String encrypt(String plainText) throws Exception {
        SecretKey secretKey = generateSecretKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedText) throws Exception {
        SecretKey secretKey = generateSecretKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(SECRET_KEY_ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY_BYTES, SECRET_KEY_ALGORITHM);
        return secretKey;
    }
}