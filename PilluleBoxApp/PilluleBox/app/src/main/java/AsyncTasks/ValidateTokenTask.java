package AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.pillulebox.General;
import com.example.pillulebox.LogInActivity;
import com.example.pillulebox.MenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ValidateTokenTask extends AsyncTask<String, Void, Response> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = General.getURL();
    private final Context context;
    public ValidateTokenTask(Context context){
        this.context = context;
    }
    @Override
    protected Response doInBackground(String... params) {
        String token = params[0];

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "auth_token")
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        if (response != null && response.isSuccessful()) {
            try {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                boolean isValid = jsonObject.getBoolean("valid");

                if (isValid) {
                    General.toastMessage("Autenticación exitosa", context);
                    Intent intent = new Intent(context, MenuActivity.class);
                    context.startActivity(intent);
                    ((LogInActivity) context).finish();
                } else {
                    General.toastMessage("Sesión expirada", context);
                    General.deleteToken(context);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                General.toastMessage("Error al procesar la respuesta", context);
            }
        } else {
            General.toastMessage("Error de autenticación", context);
            General.deleteToken(context);
        }
    }
}