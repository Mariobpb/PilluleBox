package AsyncTasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.General;

import org.json.JSONObject;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

import Models.ScheduleModes.SingleMode;

public class UpdateSingleModeTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "UpdateSingleModeTask";
    private final Context context;
    private final String token;
    private final String macAddress;
    private final SingleMode singleMode;
    private final UpdateCallback callback;
    private String errorMessage;

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }

    public UpdateSingleModeTask(Context context, String token, String macAddress,
                                SingleMode singleMode, UpdateCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.singleMode = singleMode;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(General.getURL() + "update_single_mode/" + macAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", singleMode.getId());
            jsonBody.put("medicine_name", singleMode.getMedicineName());
            jsonBody.put("dispensing_date", dateFormat.format(singleMode.getDispensingDate()));

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "Error updating single mode: " + e.getMessage(), e);
            errorMessage = e.getMessage();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            callback.onSuccess();
        } else {
            callback.onError(errorMessage != null ? errorMessage : "Error desconocido");
        }
    }
}