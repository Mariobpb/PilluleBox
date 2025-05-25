package AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pillulebox.GeneralInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Models.History;

public class GetHistoryTask extends AsyncTask<Void, Void, List<History>> {
    private static final String TAG = "GetHistoryTask";
    private Context context;
    private String token;
    private String mac;
    private HistoryCallback callback;
    private String errorMessage;

    public interface HistoryCallback {
        void onHistoryLoaded(List<History> historyList);
        void onError(String error);
    }

    public GetHistoryTask(Context context, String token, String mac, HistoryCallback callback) {
        this.context = context;
        this.token = token;
        this.mac = mac;
        this.callback = callback;
    }

    @Override
    protected List<History> doInBackground(Void... voids) {
        List<History> historyList = new ArrayList<>();

        try {
            String urlString = GeneralInfo.getURL() + "history/" + mac;
            URL url = new URL(urlString);
            Log.d(TAG, "Requesting URL: " + urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d(TAG, "Response: " + response.toString());

                JSONArray jsonArray = new JSONArray(response.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    History history = new History();
                    history.setId(jsonObject.getInt("id"));
                    history.setMacDispenser(jsonObject.getString("mac_dispenser"));
                    history.setMedicineName(jsonObject.getString("medicine_name"));
                    history.setConsumptionStatus(jsonObject.getString("consumption_status"));
                    history.setDateConsumption(jsonObject.getString("date_consumption"));
                    history.setReason(jsonObject.optString("reason", ""));

                    historyList.add(history);
                }

            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;

                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();

                Log.e(TAG, "Error Response: " + errorResponse.toString());

                try {
                    JSONObject errorJson = new JSONObject(errorResponse.toString());
                    errorMessage = errorJson.optString("error", "Error desconocido");
                } catch (JSONException e) {
                    errorMessage = "Error al obtener el historial";
                }
            }

            connection.disconnect();

        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            errorMessage = "Error de conexión";
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
            errorMessage = "Error al procesar los datos";
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            errorMessage = "Error inesperado";
        }

        return historyList;
    }

    @Override
    protected void onPostExecute(List<History> historyList) {
        super.onPostExecute(historyList);

        if (callback != null) {
            if (errorMessage != null) {
                callback.onError(errorMessage);
            } else {
                callback.onHistoryLoaded(historyList);
            }
        }
    }
}