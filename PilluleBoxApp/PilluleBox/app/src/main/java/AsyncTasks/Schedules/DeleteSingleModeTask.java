package AsyncTasks.Schedules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.GeneralInfo;

import java.net.HttpURLConnection;
import java.net.URL;

import Models.ScheduleModes.SingleMode;

public class DeleteSingleModeTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "DeleteSingleModeTask";
    private final Context context;
    private final String token;
    private final String macAddress;
    private final SingleMode singleMode;
    private final DeleteCallback callback;
    private String errorMessage;

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }

    public DeleteSingleModeTask(Context context, String token, String macAddress,
                                SingleMode singleMode, DeleteCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.singleMode = singleMode;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(GeneralInfo.getURL() + "delete_single_mode/" + macAddress + "/" + singleMode.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "Error deleting single mode: " + e.getMessage(), e);
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