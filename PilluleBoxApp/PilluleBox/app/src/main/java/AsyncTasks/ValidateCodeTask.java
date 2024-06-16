package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.pillulebox.General;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ValidateCodeTask extends AsyncTask<String, Void, Boolean> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = General.getURL();
    private final SendCodeCallback callback;
    private final Context context;
    private final TextView error;

    public ValidateCodeTask(Context context, SendCodeCallback callback, TextView error) {
        this.context = context;
        this.callback = callback;
        this.error = error;
    }

    @Override
    protected Boolean doInBackground(String... params) {
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
                .url(BASE_URL + "validateCode")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            if (response.isSuccessful() && jsonObject.has("validated") && jsonObject.getBoolean("validated")) {
                return true;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (callback != null) {
            callback.onCodeValidated(success);
        }
    }
}