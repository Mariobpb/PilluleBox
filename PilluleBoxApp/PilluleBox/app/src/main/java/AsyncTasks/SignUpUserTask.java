package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

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
    private final String BASE_URL = "http://192.168.100.14:8080/";
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
                toastMessage("Usuario registrado exitosamente");
            } else {
                int statusCode = response.code();
                try {
                    String errorMessage = response.body().string();
                    if (statusCode == 409) {
                        error_text.setText(errorMessage);
                    } else {
                        toastMessage("Error al registrar al usuario");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            toastMessage("Error de conexión con el servidor");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}