package AsyncTasks.Schedules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import Models.ScheduleModes.BasicMode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetBasicModesTask extends AsyncTask<Void, Void, List<BasicMode>> {
    private static final String TAG = "GetBasicModesTask";
    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    private final String token;
    private final String macAddress;
    private final BasicModesCallback callback;

    public interface BasicModesCallback {
        void onModesLoaded(List<BasicMode> modes);
        void onError(String error);
    }

    public GetBasicModesTask(Context context, String token, String macAddress, BasicModesCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.callback = callback;
    }

    @Override
    protected List<BasicMode> doInBackground(Void... voids) {
        List<BasicMode> modes = new ArrayList<>();

        Request request = new Request.Builder()
                .url(GeneralInfo.getURL() + "basic_modes/" + macAddress)
                .addHeader("Authorization", token)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONArray modesArray = new JSONArray(response.body().string());

                for (int i = 0; i < modesArray.length(); i++) {
                    JSONObject modeObj = modesArray.getJSONObject(i);

                    Time morningStartTime = null;
                    Time morningEndTime = null;
                    Time afternoonStartTime = null;
                    Time afternoonEndTime = null;
                    Time nightStartTime = null;
                    Time nightEndTime = null;

                    if (!modeObj.isNull("morning_start_time")) {
                        morningStartTime = Time.valueOf(modeObj.getString("morning_start_time"));
                    }
                    if (!modeObj.isNull("morning_end_time")) {
                        morningEndTime = Time.valueOf(modeObj.getString("morning_end_time"));
                    }
                    if (!modeObj.isNull("afternoon_start_time")) {
                        afternoonStartTime = Time.valueOf(modeObj.getString("afternoon_start_time"));
                    }
                    if (!modeObj.isNull("afternoon_end_time")) {
                        afternoonEndTime = Time.valueOf(modeObj.getString("afternoon_end_time"));
                    }
                    if (!modeObj.isNull("night_start_time")) {
                        nightStartTime = Time.valueOf(modeObj.getString("night_start_time"));
                    }
                    if (!modeObj.isNull("night_end_time")) {
                        nightEndTime = Time.valueOf(modeObj.getString("night_end_time"));
                    }

                    BasicMode mode = new BasicMode(
                            modeObj.getInt("id"),
                            modeObj.getString("medicine_name"),
                            morningStartTime,
                            morningEndTime,
                            afternoonStartTime,
                            afternoonEndTime,
                            nightStartTime,
                            nightEndTime
                    );
                    modes.add(mode);
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading basic modes: " + e.getMessage(), e);
        }

        return modes;
    }

    @Override
    protected void onPostExecute(List<BasicMode> modes) {
        if (modes != null && !modes.isEmpty()) {
            callback.onModesLoaded(modes);
        } else {
            callback.onError("Sin horarios disponibles");
        }
    }
}