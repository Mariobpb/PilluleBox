package AsyncTasks;

import android.os.AsyncTask;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendCodeTask extends AsyncTask<String, Void, Boolean> {
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = GeneralInfo.getURL();
    private final CallbackValidations callback;

    public SendCodeTask(CallbackValidations callback) {
        this.callback = callback;
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
                .url(BASE_URL + "sendCode")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                return jsonObject.getBoolean("sent");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (callback != null) {
            callback.onCodeSent(success);
        }
    }
}