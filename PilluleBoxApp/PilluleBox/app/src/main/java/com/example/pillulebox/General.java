package com.example.pillulebox;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.Toast;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class General {
    private static final String Secret_Key = "1234567890123456"; // Secret Key (16 bytes)
    private static final String IV = "iughvnbaklsvvkhj"; // initialization vector (16 bytes)
    private static final String BASE_URL = "http://192.168.43.234:8080/";
    public static final String Archivo = "PilluleBoxPrefs";
    public static final String Key = "AuthToken";
    public static String encryptPassword(String password) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(Secret_Key.getBytes(), "AES");
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
    public static void setToken(Context context, String token) {
        SharedPreferences sharedPref = context.getSharedPreferences(Archivo, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Key, token);
        editor.apply();
    }
    public static String getToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Archivo, Context.MODE_PRIVATE);
        return sharedPref.getString(Key, null);
    }
    public static void deleteToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Archivo, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Key);
        editor.apply();
    }
    public static String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
