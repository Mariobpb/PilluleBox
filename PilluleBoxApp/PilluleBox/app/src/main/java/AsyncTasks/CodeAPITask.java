package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pillulebox.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CodeAPITask extends AsyncTask<String, Void, Response> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = Functions.getURL();
    private final Context context;

    public CodeAPITask(Context context) {
        this.context = context;
    }

    @Override
    protected Response doInBackground(String... params) {
        String code = params[0];
        String email = params[1];

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("code", code);
            jsonBody.put("email", email);
            // Agregar fecha y hora de creación y expiración
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "register-code")
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
            // El código se registró correctamente en la API
        } else {
            // Hubo un error al registrar el código en la API
        }
    }
}