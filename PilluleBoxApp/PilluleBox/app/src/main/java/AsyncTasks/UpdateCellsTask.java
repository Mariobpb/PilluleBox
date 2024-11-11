package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import Models.Cell;

public class UpdateCellsTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "UpdateCellsTask";
    private final Context context;
    private final String token;
    private final String macAddress;
    private final List<Cell> cells;
    private final UpdateCallback callback;
    private String errorMessage;

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }

    public UpdateCellsTask(Context context, String token, String macAddress,
                           List<Cell> cells, UpdateCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.cells = cells;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(GeneralInfo.getURL() + "update_cells/" + macAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONArray jsonArray = new JSONArray();
            for (Cell cell : cells) {
                JSONObject cellJson = new JSONObject();
                cellJson.put("id", cell.getId());
                cellJson.put("mac_dispenser", cell.getMacDispenser());
                cellJson.put("num_cell", cell.getNumCell());
                cellJson.put("single_mode_id", cell.getSingleModeId());
                cellJson.put("sequential_mode_id", cell.getSequentialModeId());
                cellJson.put("basic_mode_id", cell.getBasicModeId());
                jsonArray.put(cellJson);
            }

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("cells", jsonArray);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "Error updating cells: " + e.getMessage(), e);
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