package AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pillulebox.EmailActivity;
import com.example.pillulebox.Functions;
import com.example.pillulebox.SignUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpUserTask extends AsyncTask<String, Void, Response> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = Functions.getURL();
    Context context;
    TextView error_text;
    public SignUpUserTask(Context context, TextView error_text){
        this.context = context;
        this.error_text = error_text;
    }
    @Override
    protected Response doInBackground(String... params) {
        String username = params[0];
        String email = params[1];
        String password = params[2];

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "signup")
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
        if (response != null) {
            if (response.isSuccessful()) {
                Functions.toastMessage("Usuario registrado exitosamente", context);
                Intent intent = new Intent(context, EmailActivity.class);
                context.startActivity(intent);
            } else {
                int statusCode = response.code();
                try {
                    String errorMessageBody = response.body().string();
                    if (statusCode == 409) {
                        JSONObject jsonObject = new JSONObject(errorMessageBody);
                        error_text.setText(jsonObject.getString("error"));
                    } else {
                        Functions.toastMessage("Error al registrar al usuario", context);
                    }
                } catch (IOException | JSONException e) {
                    Functions.toastMessage(e.toString(), context);
                }
            }
        } else {
            Functions.toastMessage("Error de conexión con el servidor", context);
        }
    }
}