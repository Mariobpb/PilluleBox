package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.General;
import com.example.pillulebox.adapters.DispenserAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Classes.Dispenser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetUserDispensersTask extends AsyncTask<Void, Void, List<Dispenser>> {
    private static final String TAG = "GetUserDispensersTask";
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
    protected List<Dispenser> doInBackground(Void... voids) {
        List<Dispenser> dispensers = new ArrayList<>();
        Log.d(TAG, "Iniciando petición al servidor...");

        Request request = new Request.Builder()
                .url(General.getURL() + "user_dispensers")
                .addHeader("Authorization", token)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, "Código de respuesta: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                Log.d(TAG, "Respuesta del servidor: " + responseBody);

                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray macArray = jsonResponse.getJSONArray("macAddresses");
                JSONArray namesArray = jsonResponse.getJSONArray("names");
                JSONArray contextsArray = jsonResponse.getJSONArray("contexts");

                Log.d(TAG, "Número de dispensadores recibidos: " + macArray.length());

                for (int i = 0; i < macArray.length(); i++) {
                    String mac = macArray.getString(i);
                    String name = namesArray.getString(i);
                    int context = -1;

                    if (!contextsArray.isNull(i)) {
                        context = contextsArray.getInt(i);
                    }

                    Log.d(TAG, String.format("Creando dispenser %d: MAC=%s, Name=%s, Context=%d",
                            i, mac, name, context));

                    dispensers.add(new Dispenser(mac, name, context));
                }
            } else {
                Log.e(TAG, "Error en la respuesta del servidor: " + response.code());
                if (response.body() != null) {
                    Log.e(TAG, "Mensaje de error: " + response.body().string());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error de red: " + e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(TAG, "Error al procesar JSON: " + e.getMessage(), e);
        }

        Log.d(TAG, "Número total de dispensadores procesados: " + dispensers.size());
        return dispensers;
    }

    @Override
    protected void onPostExecute(List<Dispenser> dispensers) {
        if (dispensers != null && !dispensers.isEmpty()) {
            Log.d(TAG, "Configurando adapter con " + dispensers.size() + " dispensadores");
            DispenserAdapter adapter = new DispenserAdapter(dispensers);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "No se encontraron dispensadores o la lista es nula");
            if (context != null) {
                General.toastMessage("No se encontraron dispensadores", context);
            }
        }
    }
}