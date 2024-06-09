package com.example.pillulebox;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthenticateUserTask extends AsyncTask<String, Void, Response> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = "http://192.168.100.14:8080/"; // Actualiza con tu URL
    Context contextMain;
    public AuthenticateUserTask(Context context){
        contextMain = context;
    }
    @Override
    protected Response doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "auth")
                .post(requestBody)
                .build();

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Response response) {
        if (response != null && response.isSuccessful()) {
            // Autenticación exitosa
            toastMessage("Autenticación exitosa");
        } else {
            // Autenticación fallida
            toastMessage("Autenticación fallida");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(contextMain, message, Toast.LENGTH_SHORT).show();
    }
}