package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateDispenserContextTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "UpdateDispenserContextTask";
    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    private final String token;
    private final String macAddress;
    private final int newContext;
    private final OnContextUpdateListener listener;

    public interface OnContextUpdateListener {
        void onContextUpdateSuccess();
        void onContextUpdateError(String message);
    }

    public UpdateDispenserContextTask(Context context, String token, String macAddress,
                                      int newContext, OnContextUpdateListener listener) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.newContext = newContext;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mac_address", macAddress);
            jsonBody.put("context", newContext);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body: " + e.getMessage());
            return false;
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonBody.toString()
        );

        Request request = new Request.Builder()
                .url(GeneralInfo.getURL() + "update_dispenser_context")
                .addHeader("Authorization", token)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            Log.e(TAG, "Error during network request: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            listener.onContextUpdateSuccess();
        } else {
            listener.onContextUpdateError("Error al actualizar el contexto del dispensador");
        }
    }
}