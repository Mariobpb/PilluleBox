package AsyncTasks.Schedules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.GeneralInfo;

import java.net.HttpURLConnection;
import java.net.URL;

import Models.ScheduleModes.BasicMode;

public class DeleteBasicModeTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "DeleteBasicModeTask";
    private final Context context;
    private final String token;
    private final String macAddress;
    private final BasicMode basicMode;
    private final DeleteCallback callback;
    private String errorMessage;

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }

    public DeleteBasicModeTask(Context context, String token, String macAddress,
                               BasicMode basicMode, DeleteCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.basicMode = basicMode;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(GeneralInfo.getURL() + "delete_basic_mode/" + macAddress + "/" + basicMode.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "Error deleting basic mode: " + e.getMessage(), e);
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