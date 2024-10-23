package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.General;
import com.example.pillulebox.adapters.MacAddressAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetUserDispensersTask extends AsyncTask<Void, Void, List<String>> {
    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    private final RecyclerView recyclerView;
    private final String token;

    public GetUserDispensersTask(Context context, RecyclerView recyclerView, String token) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.token = token;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        List<String> macAddresses = new ArrayList<>();

        Request request = new Request.Builder()
                .url(General.getURL() + "user_dispensers")
                .addHeader("Authorization", token)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                JSONArray macArray = jsonResponse.getJSONArray("macAddresses");

                for (int i = 0; i < macArray.length(); i++) {
                    macAddresses.add(macArray.getString(i));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return macAddresses;
    }

    @Override
    protected void onPostExecute(List<String> macAddresses) {
        if (macAddresses != null && !macAddresses.isEmpty()) {
            MacAddressAdapter adapter = new MacAddressAdapter(macAddresses);
            recyclerView.setAdapter(adapter);
        } else {
            General.toastMessage("No se encontraron dispositivos", context);
        }
    }
}