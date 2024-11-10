package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.Cell;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetDispenserCellsTask extends AsyncTask<Void, Void, List<Cell>> {
    private static final String TAG = "GetDispenserCellsTask";
    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    private final String token;
    private final String macAddress;
    private final CellsCallback callback;

    public interface CellsCallback {
        void onCellsLoaded(List<Cell> cells);
        void onError(String error);
    }

    public GetDispenserCellsTask(Context context, String token, String macAddress, CellsCallback callback) {
        this.context = context;
        this.token = token;
        this.macAddress = macAddress;
        this.callback = callback;
    }

    @Override
    protected List<Cell> doInBackground(Void... voids) {
        List<Cell> cells = new ArrayList<>();
        Log.d(TAG, "Iniciando petición al servidor para obtener celdas del dispensador: " + macAddress);

        Request request = new Request.Builder()
                .url(GeneralInfo.getURL() + "dispenser_cells/" + macAddress)
                .addHeader("Authorization", token)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, "Código de respuesta: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                Log.d(TAG, "Respuesta del servidor: " + responseBody);

                JSONArray cellsArray = new JSONArray(responseBody);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                for (int i = 0; i < cellsArray.length(); i++) {
                    JSONObject cellObj = cellsArray.getJSONObject(i);

                    Cell cell = new Cell(
                            cellObj.getInt("id"),
                            cellObj.getString("mac_dispenser"),
                            cellObj.getInt("num_cell"),
                            cellObj.isNull("current_medicine_date") ? null :
                                    Date.from(Instant.parse(cellObj.getString("current_medicine_date"))),
                            cellObj.isNull("single_mode_id") ? null : cellObj.getInt("single_mode_id"),
                            cellObj.isNull("sequential_mode_id") ? null : cellObj.getInt("sequential_mode_id"),
                            cellObj.isNull("basic_mode_id") ? null : cellObj.getInt("basic_mode_id")
                    );

                    cells.add(cell);
                }
            } else {
                Log.e(TAG, "Error en la respuesta del servidor: " + response.code());
                if (response.body() != null) {
                    Log.e(TAG, "Mensaje de error: " + response.body().string());
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error al procesar la respuesta: " + e.getMessage(), e);
        }

        return cells;
    }

    @Override
    protected void onPostExecute(List<Cell> cells) {
        if (cells != null && !cells.isEmpty()) {
            Log.d(TAG, "Celdas cargadas exitosamente: " + cells.size() + " celdas");
            callback.onCellsLoaded(cells);
        } else {
            Log.e(TAG, "No se encontraron celdas o hubo un error");
            callback.onError("No se pudieron cargar las celdas del dispensador");
        }
    }
}