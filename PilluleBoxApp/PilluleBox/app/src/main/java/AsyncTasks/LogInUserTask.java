package AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.pillulebox.GeneralInfo;
import com.example.pillulebox.LogInActivity;
import com.example.pillulebox.MenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogInUserTask extends AsyncTask<String, Void, Response> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = GeneralInfo.getURL();
    private final String secretKey;
    public String token;
    private final Context context;
    TextView error_text;
    public LogInUserTask(Context context, TextView error_text){
        this.context = context;
        this.error_text = error_text;
        this.secretKey = generateSecretKey();
    }
    @Override
    protected Response doInBackground(String... params) {
        String username_email = params[0];
        String password = params[1];

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username_email", username_email);
            jsonBody.put("password", password);
            jsonBody.put("secretKey", secretKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "login")
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                token = jsonObject.getString("token");
                return response;
            } else {
                return null;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        if (response != null) {
            if(response.isSuccessful()){
                error_text.setText("");
                GeneralInfo.toastMessage("Autenticación exitosa", context);

                GeneralInfo.setToken(context, token);

                Intent intent = new Intent(context, MenuActivity.class);
                context.startActivity(intent);
                ((LogInActivity) context).finish();
            } else {
                GeneralInfo.toastMessage("Error de autenticación", context);
            }
        } else {
            GeneralInfo.toastMessage("Error de comunicación con el servidor", context);
        }
    }

    private String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return bytesToHex(randomBytes);
    }
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}