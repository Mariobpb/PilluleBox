package AsyncTasks;

import android.content.Context;
import android.icu.text.SimpleDateFormat;

import java.sql.Date;
import java.text.ParseException;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.General;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.ScheduleModes.SingleMode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetSingleModesTask extends AsyncTask<Void, Void, List<SingleMode>> {
    private static final String TAG = "GetSingleModesTask";
    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    private final String token;
    private final String macAddress;
    private final SingleModesCallback callback;

    public interface SingleModesCallback {
        void onModesLoaded(List<SingleMode> modes);
        void onError(String error);
    }

    public GetSingleModesTask(Context context, String token, String macAddress, SingleModesCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.callback = callback;
    }

    @Override
    protected List<SingleMode> doInBackground(Void... voids) {
        List<SingleMode> modes = new ArrayList<>();

        Request request = new Request.Builder()
                .url(General.getURL() + "single_modes/" + macAddress)
                .addHeader("Authorization", token)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONArray modesArray = new JSONArray(response.body().string());

                for (int i = 0; i < modesArray.length(); i++) {
                    JSONObject modeObj = modesArray.getJSONObject(i);
                    SingleMode mode = new SingleMode(
                            modeObj.getInt("id"),
                            modeObj.getString("medicine_name"),
                            Date.from(Instant.parse(modeObj.getString("dispensing_date")))
                    );
                    modes.add(mode);
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading single modes: " + e.getMessage(), e);
        }

        return modes;
    }

    @Override
    protected void onPostExecute(List<SingleMode> modes) {
        if (modes != null && !modes.isEmpty()) {
            callback.onModesLoaded(modes);
        } else {
            callback.onError("No se pudieron cargar los modos");
        }
    }
}