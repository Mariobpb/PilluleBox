package AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.pillulebox.EmailActivity;
import com.example.pillulebox.General;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendCodeTask extends AsyncTask<String, Void, Response> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = General.getURL();
    private final String username, email, password;
    private final Context context;

    public SendCodeTask(Context context, String username, String email, String password) {
        this.context = context;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    protected Response doInBackground(String... params) {
        String code = params[0];
        String email = params[1];

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("code", code);
            jsonBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "sendCode")
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
            try {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);

                if (response.isSuccessful()) {
                    if (jsonObject.getBoolean("sent")) {
                        General.toastMessage("Código enviado correctamente", context);
                        Intent intent = new Intent(context, EmailActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        context.startActivity(intent);
                    } else {
                        General.toastMessage("Error al enviar el código", context);
                    }
                } else {
                    String errorMessage = jsonObject.optString("error", "Error desconocido");
                    General.toastMessage("Error: " + errorMessage, context);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                General.toastMessage("Error al procesar la respuesta del servidor", context);
            }
        } else {
            General.toastMessage("No se pudo conectar con el servidor", context);
        }
    }
}