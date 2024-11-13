package AsyncTasks.Schedules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Models.ScheduleModes.SequentialMode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetSequentialModesTask extends AsyncTask<Void, Void, List<SequentialMode>> {
    private static final String TAG = "GetSequentialModesTask";
    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    private final String token;
    private final String macAddress;
    private final SequentialModesCallback callback;

    public interface SequentialModesCallback {
        void onModesLoaded(List<SequentialMode> modes);
        void onError(String error);
    }

    public GetSequentialModesTask(Context context, String token, String macAddress, SequentialModesCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.callback = callback;
    }

    @Override
    protected List<SequentialMode> doInBackground(Void... voids) {
        List<SequentialMode> modes = new ArrayList<>();

        Request request = new Request.Builder()
                .url(GeneralInfo.getURL() + "sequential_modes/" + macAddress)
                .addHeader("Authorization", token)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONArray modesArray = new JSONArray(response.body().string());

                for (int i = 0; i < modesArray.length(); i++) {
                    JSONObject modeObj = modesArray.getJSONObject(i);

                    Date startDate = null;
                    Date endDate = null;

                    if (!modeObj.isNull("start_date")) {
                        String startDateStr = modeObj.getString("start_date");
                        Timestamp startTimestamp = Timestamp.valueOf(startDateStr.replace("T", " ").replace("Z", ""));
                        startDate = new Date(startTimestamp.getTime());
                    }

                    if (!modeObj.isNull("end_date")) {
                        String endDateStr = modeObj.getString("end_date");
                        Timestamp endTimestamp = Timestamp.valueOf(endDateStr.replace("T", " ").replace("Z", ""));
                        endDate = new Date(endTimestamp.getTime());
                    }

                    Time period = null;
                    if (!modeObj.isNull("period")) {
                        String periodStr = modeObj.getString("period");
                        try {
                            period = Time.valueOf(periodStr);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing period: " + e.getMessage(), e);
                        }
                    }

                    SequentialMode mode = new SequentialMode(
                            modeObj.getInt("id"),
                            modeObj.getString("medicine_name"),
                            startDate,
                            endDate,
                            period,
                            modeObj.getInt("limit_times_consumption"),
                            modeObj.getInt("affected_periods") == 1,
                            modeObj.getInt("current_times_consumption")
                    );
                    modes.add(mode);
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading sequential modes: " + e.getMessage(), e);
        }

        return modes;
    }

    @Override
    protected void onPostExecute(List<SequentialMode> modes) {
        if (modes != null && !modes.isEmpty()) {
            callback.onModesLoaded(modes);
        } else {
            callback.onError("Sin horarios disponibles ");
        }
    }
}