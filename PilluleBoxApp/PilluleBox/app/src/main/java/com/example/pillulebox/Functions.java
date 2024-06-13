package com.example.pillulebox;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class Functions {
    private static final String SECRET_KEY = "1234567890123456"; // Secret Key (16 bytes)
    private static final String IV = "iughvnbaklsvvkhj"; // initialization vector (16 bytes)
    private static final String BASE_URL = "http://192.168.100.14:8080/";
    public static String encryptPassword(String password) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(password.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }
    public static void toastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getURL(){
        return BASE_URL;
    }
}
