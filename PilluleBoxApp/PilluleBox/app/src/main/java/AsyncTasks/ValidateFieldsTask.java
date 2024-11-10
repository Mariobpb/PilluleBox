package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ValidateFieldsTask extends AsyncTask<String, Void, Boolean> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = GeneralInfo.getURL();
    private final CallbackValidations callback;
    private final Context context;
    private final TextView error;
    private String errorMessage;

    public ValidateFieldsTask(Context context, CallbackValidations callback, TextView error) {
        this.context = context;
        this.callback = callback;
        this.error = error;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String username = params[0];
        String email = params[1];

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
        } catch (JSONException e) {
            errorMessage = "Error al procesar la solicitud";
            return false;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "validate_fields")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            JSONObject jsonObject = new JSONObject(responseBody);
            if (response.isSuccessful()) {
                if (jsonObject.has("validated") && jsonObject.getBoolean("validated")) {
                    error.post(() -> error.setText(""));
                    return true;
                } else {
                    final String errorText = jsonObject.getString("error");
                    error.post(() -> error.setText(errorText));
                }
            } else {
                final String errorText = jsonObject.getString("error");
                error.post(() -> error.setText(errorText));
            }
        } catch (IOException e) {
            error.post(() -> error.setText("Error de conexión con el servidor"));
            Log.e("ValidateFieldsTask", "Error de conexión", e);
            return false;
        } catch (JSONException e) {
            error.post(() -> error.setText("Error al procesar la respuesta del servidor"));
            Log.e("ValidateFieldsTask", "Error al procesar JSON", e);
            return false;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (!success && errorMessage != null) {
            GeneralInfo.toastMessage(errorMessage, context);
        }
        if (callback != null) {
            callback.onFieldsValidated(success);
        }
    }
}