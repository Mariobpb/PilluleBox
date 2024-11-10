package AsyncTasks.Schedules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

import Models.ScheduleModes.BasicMode;

public class UpdateBasicModeTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "UpdateBasicModeTask";
    private final Context context;
    private final String token;
    private final String macAddress;
    private final BasicMode basicMode;
    private final UpdateCallback callback;
    private String errorMessage;

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }

    public UpdateBasicModeTask(Context context, String token, String macAddress,
                               BasicMode basicMode, UpdateCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.basicMode = basicMode;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(GeneralInfo.getURL() + "update_basic_mode/" + macAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", basicMode.getId());
            jsonBody.put("medicine_name", basicMode.getMedicineName());
            jsonBody.put("morning_start_time", timeFormat.format(basicMode.getMorningStartTime()));
            jsonBody.put("morning_end_time", timeFormat.format(basicMode.getMorningEndTime()));

            jsonBody.put("afternoon_start_time", timeFormat.format(basicMode.getAfternoonStartTime()));
            jsonBody.put("afternoon_end_time", timeFormat.format(basicMode.getAfternoonEndTime()));

            jsonBody.put("night_start_time", timeFormat.format(basicMode.getNightStartTime()));
            jsonBody.put("night_end_time", timeFormat.format(basicMode.getNightEndTime()));

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "Error updating basic mode: " + e.getMessage(), e);
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